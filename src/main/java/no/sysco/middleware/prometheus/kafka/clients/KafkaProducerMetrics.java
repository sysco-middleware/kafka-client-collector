package no.sysco.middleware.prometheus.kafka.clients;

import org.apache.kafka.clients.producer.Producer;

public final class KafkaProducerMetrics extends KafkaMetrics {
  public KafkaProducerMetrics(Producer<?, ?> kafkaProducer) { super(kafkaProducer::metrics); }
}
