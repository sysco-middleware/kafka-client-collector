package no.sysco.middleware.prometheus.kafka.example;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import no.sysco.middleware.prometheus.kafka.KafkaClientsJmxCollector;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class Producer {
    public static void main(String[] args) throws Exception {

        String id = UUID.randomUUID().toString();
        final Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, id);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        properties.put(ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG, StringSerializer.class);

        final String topic = "topic-in";

        final KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        Map<MetricName, ? extends Metric> metrics = kafkaProducer.metrics();
        for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
            System.out.println(entry.getKey());
            KafkaMetric value = (KafkaMetric) entry.getValue();
            System.out.println(value.metricValue());
            System.out.println();
        }

//        System.out.println(metrics);
        DefaultExports.initialize();
        HTTPServer server = new HTTPServer(8080);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        String domain = "kafka.producer";
        KafkaClientsJmxCollector kafkaClientsJmxCollector = new KafkaClientsJmxCollector(kafkaProducer.metrics().keySet());
        boolean b = kafkaClientsJmxCollector.validateMbeanObject(domain,"producer-metrics", id);
        System.out.println(b);
//        kafkaClientsJmxCollector.register();
//        boolean b = kafkaClientsJmxCollector.validateMbeanObject("producer-metrics");
//        Double mBeanAttributeValue = kafkaClientsJmxCollector.getMBeanAttributeValue("kafka.producer","producer-metrics", "record-send-total", id,  Double.class);
//        System.out.println(mBeanAttributeValue);
        kafkaClientsJmxCollector.register();

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        System.out.println("Here " + Arrays.asList(mBeanServer.getDomains()));

        while(true){
            Thread.sleep(1_000);
            long now = Instant.now().toEpochMilli();
            kafkaProducer.send(
                    new ProducerRecord<String, String>(topic, String.valueOf(now), now + " milliseconds"),
                    (metadata, exception)-> {
                        if (exception==null){
                            System.out.println("successfully sent");
                        } else {
                            System.out.println("fail sent");
                        }
                    }
            );
        }
    }
}
