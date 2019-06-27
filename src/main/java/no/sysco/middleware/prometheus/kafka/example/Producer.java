package no.sysco.middleware.prometheus.kafka.example;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import no.sysco.middleware.prometheus.kafka.ClientsJmxCollector;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.*;

public class Producer {
    public static void main(String[] args) throws Exception {

        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();

//        properties.put(ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG, StringSerializer.class);

        final String topic = "topic-in";

        final KafkaProducer<String, String> kafkaProducer1 = new KafkaProducer<>(getProducerProps(id1));
        final KafkaProducer<String, String> kafkaProducer2 = new KafkaProducer<>(getProducerProps(id2));

//        Map<MetricName, ? extends Metric> metrics = kafkaProducer1.metrics();
//        for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
//            System.out.println(entry.getKey());
//            KafkaMetric value = (KafkaMetric) entry.getValue();
//            System.out.println(value.metricValue());
//            System.out.println();
//        }

//        System.out.println(metrics);
        DefaultExports.initialize();
        HTTPServer server = new HTTPServer(8080);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        String domain = "kafka.producer";
        Set<MetricName> metrics1 = kafkaProducer1.metrics().keySet();
        Set<MetricName> metrics2 = kafkaProducer2.metrics().keySet();
        HashSet<MetricName> metrics = new HashSet<MetricName>() {{
                addAll(metrics1);
                addAll(metrics2);
            }};

        //.addAll(kafkaProducer2.metrics().keySet());
        ClientsJmxCollector clientsJmxCollector = new ClientsJmxCollector(metrics);
//        boolean b = clientsJmxCollector.validateMbeanObject(domain,"producer-metrics", id);
//        System.out.println(b);
//        clientsJmxCollector.register();
//        boolean b = clientsJmxCollector.validateMbeanObject("producer-metrics");
//        Double mBeanAttributeValue = clientsJmxCollector.getMBeanAttributeValue("kafka.producer","producer-metrics", "record-send-total", id,  Double.class);
//        System.out.println(mBeanAttributeValue);
        clientsJmxCollector.register();

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        System.out.println("Here " + Arrays.asList(mBeanServer.getDomains()));

        while(true){
            Thread.sleep(1_000);
            long now = Instant.now().toEpochMilli();
            kafkaProducer1.send(
                    new ProducerRecord<String, String>(topic, String.valueOf(now) + " p1", now + " milliseconds"),
                    (metadata, exception)-> {
                        if (exception==null){
                            System.out.println("successfully sent");
                        } else {
                            System.out.println("fail sent");
                        }
                    }
            );

            kafkaProducer2.send(
                    new ProducerRecord<String, String>(topic, String.valueOf(now) + " p2", now + " milliseconds"),
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

    static Properties getProducerProps(String id) {
        final Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, id);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return properties;
    }
}
