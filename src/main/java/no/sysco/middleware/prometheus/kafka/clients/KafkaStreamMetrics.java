package no.sysco.middleware.prometheus.kafka.clients;

import org.apache.kafka.streams.KafkaStreams;

public class KafkaStreamMetrics extends KafkaMetrics {
  public KafkaStreamMetrics(KafkaStreams kafkaStreams) { super(kafkaStreams::metrics); }
}
