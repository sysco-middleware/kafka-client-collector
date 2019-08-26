package no.sysco.middleware.prometheus.kafka.clients;

import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class KafkaConsumerMetricsTest {
  private static final String BOOTSTRAP_SERVERS = "localhost:29092";

  @Test
  public void verify() {
    String id = UUID.randomUUID().toString();
    String groupId = "some-gr-id";

    try (Consumer<String, String> consumer = createConsumer(id, groupId)) {
      KafkaConsumerMetrics metrics = new KafkaConsumerMetrics(consumer);
      assertTrue(
          metrics.getAllMetrics().stream()
              .anyMatch(
                  metricFamilySamples ->
                      "consumer_metrics_iotime_total".equals(metricFamilySamples.name)));
    } catch (Exception e) {
      fail();
    }
  }

  private Consumer<String, String> createConsumer(String id, String groupId) {
    final Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    properties.put(ConsumerConfig.CLIENT_ID_CONFIG, id);
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    return new KafkaConsumer<>(properties);
  }
}
