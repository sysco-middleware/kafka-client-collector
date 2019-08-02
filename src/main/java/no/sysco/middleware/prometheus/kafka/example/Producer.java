package no.sysco.middleware.prometheus.kafka.example;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import no.sysco.middleware.prometheus.kafka.KafkaClientsJmxExports;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {
  public static void main(String[] args) throws Exception {

    String id1 = UUID.randomUUID().toString();
    String id2 = UUID.randomUUID().toString();
    final String topic1 = "topic-1";
    final String topic2 = "topic-2";
    final KafkaProducer<String, String> kafkaProducer1 = new KafkaProducer<>(getProducerProps(id1));
    final KafkaProducer<String, String> kafkaProducer2 = new KafkaProducer<>(getProducerProps(id2));

    DefaultExports.initialize();
    HTTPServer server = new HTTPServer(8081);
    Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

    // Set<MetricName> metrics1 = kafkaProducer1.metrics().keySet();
    // Set<MetricName> metrics2 = kafkaProducer2.metrics().keySet();

    KafkaClientsJmxExports.initialize(kafkaProducer1);

    while (true) {
      Thread.sleep(5_000);
      long now = Instant.now().toEpochMilli();

      kafkaProducer1.send(
          new ProducerRecord<>(topic1, now + " p1", now + " milliseconds"),
          (metadata, exception) -> {
            if (exception == null) {
              System.out.println("successfully sent");
            } else {
              System.out.println("fail sent");
            }
          });
      kafkaProducer1.send(
          new ProducerRecord<>(topic2, now + " p1", now + " milliseconds"),
          (metadata, exception) -> {
            if (exception == null) {
              System.out.println("successfully sent");
            } else {
              System.out.println("fail sent");
            }
          });

      Thread.sleep(5_000);
      kafkaProducer2.send(
          new ProducerRecord<>(topic1, now + " p2", now + " milliseconds"),
          (metadata, exception) -> {
            if (exception == null) {
              System.out.println("successfully sent");
            } else {
              System.out.println("fail sent");
            }
          });
      //            printMetrics(kafkaProducer1.metrics());
    }
  }

  static Properties getProducerProps(String id) {
    final Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, id);
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return properties;
  }

  static void printMetrics(Map<MetricName, ? extends Metric> metrics) {
    for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
      System.out.println(entry.getKey());
      KafkaMetric value = (KafkaMetric) entry.getValue();
      System.out.println(value.metricValue());
      System.out.println();
    }
  }
}
