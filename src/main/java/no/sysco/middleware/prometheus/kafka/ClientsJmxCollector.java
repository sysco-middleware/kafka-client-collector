package no.sysco.middleware.prometheus.kafka;

import io.prometheus.client.Collector;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import no.sysco.middleware.prometheus.kafka.clients.KafkaConsumerMetrics;
import no.sysco.middleware.prometheus.kafka.clients.KafkaMetrics;
import no.sysco.middleware.prometheus.kafka.clients.KafkaProducerMetrics;
import no.sysco.middleware.prometheus.kafka.clients.KafkaStreamMetrics;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.streams.KafkaStreams;

public class ClientsJmxCollector extends Collector {

  private List<KafkaMetrics> clientMetricReporters;

  ClientsJmxCollector(Object... kafkaClients) {
    this.clientMetricReporters = new ArrayList<>();
    for (Object client : kafkaClients) {
      if (client instanceof Producer) {
        clientMetricReporters.add(new KafkaProducerMetrics((Producer<?, ?>) client));
      } else if (client instanceof Consumer) {
        clientMetricReporters.add(new KafkaConsumerMetrics((Consumer<?, ?>) client));
      } else if (client instanceof KafkaStreams) {
        clientMetricReporters.add(new KafkaStreamMetrics((KafkaStreams) client));
      } else {
        throw new IllegalArgumentException("Unknown client");
      }
    }
  }

  @Override
  public List<MetricFamilySamples> collect() {
    return clientMetricReporters.stream()
        .flatMap(collector -> collector.getAllMetrics().stream())
        .collect(Collectors.toList());
  }
}
