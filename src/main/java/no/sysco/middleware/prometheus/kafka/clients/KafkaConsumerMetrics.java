package no.sysco.middleware.prometheus.kafka.clients;

import org.apache.kafka.clients.consumer.Consumer;

public class KafkaConsumerMetrics extends KafkaMetrics {
  public KafkaConsumerMetrics(Consumer<?, ?> kafkaConsumer) { super(kafkaConsumer::metrics); }
}
