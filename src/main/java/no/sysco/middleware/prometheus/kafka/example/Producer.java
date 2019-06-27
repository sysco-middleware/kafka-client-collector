package no.sysco.middleware.prometheus.kafka.example;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import no.sysco.middleware.prometheus.kafka.KafkaClientsJmxExports;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.StringSerializer;
import java.time.Instant;
import java.util.*;

public class Producer {
    public static void main(String[] args) throws Exception {

        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        final String topic = "topic-in";
        final KafkaProducer<String, String> kafkaProducer1 = new KafkaProducer<>(getProducerProps(id1));
        final KafkaProducer<String, String> kafkaProducer2 = new KafkaProducer<>(getProducerProps(id2));

//        DefaultExports.initialize();
        HTTPServer server = new HTTPServer(8080);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));


        Set<MetricName> metrics1 = kafkaProducer1.metrics().keySet();
        Set<MetricName> metrics2 = kafkaProducer2.metrics().keySet();
        HashSet<MetricName> metrics = new HashSet<MetricName>() {{
                addAll(metrics1);
                addAll(metrics2);
            }};

        KafkaClientsJmxExports.initialize(metrics);


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
