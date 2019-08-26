package no.sysco.middleware.prometheus.kafka.clients;

import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class KafkaStreamMetricsTest {
  private static final String BOOTSTRAP_SERVERS = "localhost:29092";

  @Test
  public void verify() {
    String id = UUID.randomUUID().toString();
    KafkaStreams streams = createStreamApp(id);
    KafkaStreamMetrics metrics = new KafkaStreamMetrics(streams);
    assertTrue(
        metrics.getAllMetrics().stream()
            // consumer api under the hood
            .anyMatch(
                metricFamilySamples ->
                    "consumer_metrics_iotime_total".equals(metricFamilySamples.name)));
  }

  private KafkaStreams createStreamApp(String id) {
    Properties streamConfig = new Properties();
    streamConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    streamConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-metrics");
    streamConfig.put(StreamsConfig.CLIENT_ID_CONFIG, id);
    streamConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    streamConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    return new KafkaStreams(new StreamsBuilder().build(), streamConfig);
  }
}
