package no.sysco.middleware.prometheus.kafka.clients;

import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class KafkaProducerMetricsTest {

  private static final String BOOTSTRAP_SERVERS = "localhost:29092";

  @Test
  public void verify() {
    String id = UUID.randomUUID().toString();
    try (Producer<String, String> producer = createProducer(id)) {
      KafkaProducerMetrics metrics = new KafkaProducerMetrics(producer);
      assertTrue(
          metrics.getAllMetrics().stream()
              .anyMatch(
                  metricFamilySamples ->
                      "producer_metrics_network_io_total".equals(metricFamilySamples.name)));
    } catch (Exception e) {
      fail();
    }
  }

  private Producer<String, String> createProducer(String id) {
    Properties producerConfig = new Properties();
    producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, id);
    producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new KafkaProducer<>(producerConfig);
  }
}
