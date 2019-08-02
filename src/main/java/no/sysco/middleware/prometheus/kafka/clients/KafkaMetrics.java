package no.sysco.middleware.prometheus.kafka.clients;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;

public abstract class KafkaMetrics {
  private final Supplier<Map<MetricName, ? extends Metric>> metricsSupplier;

  KafkaMetrics(Supplier<Map<MetricName, ? extends Metric>> metricsSupplier) {
    this.metricsSupplier = metricsSupplier;
  }

  public List<Collector.MetricFamilySamples> getAllMetrics() {
    List<Collector.MetricFamilySamples> metricFamilySamples = new ArrayList<>();
    Map<MetricName, ? extends Metric> metrics = metricsSupplier.get();

    metrics.forEach(
        (metricName, metric) -> {
          if (metric instanceof KafkaMetric) {
            KafkaMetric kafkaMetric = (KafkaMetric) metric;
            final List<String> tagsKeys = new ArrayList<>(metric.metricName().tags().keySet());
            final List<String> tagValues = new ArrayList<>(metric.metricName().tags().values());

            if (kafkaMetric.metricName().name().endsWith("total")) {
              double value;
              if (metric.metricValue() instanceof Double) {
                value = (double) metric.metricValue();
                CounterMetricFamily counterMetricFamily =
                    new CounterMetricFamily(
                        metricName(kafkaMetric), kafkaMetric.metricName().description(), tagsKeys);
                counterMetricFamily.addMetric(tagValues, value);
                metricFamilySamples.add(counterMetricFamily);
              } else {
                System.out.println("NOT A COUNTER");
              }
            } else {
              double value;
              if (metric.metricValue() instanceof Double) {
                value = (double) metric.metricValue();
                GaugeMetricFamily gaugeMetricFamily =
                    new GaugeMetricFamily(
                        metricName(kafkaMetric), kafkaMetric.metricName().description(), tagsKeys);
                gaugeMetricFamily.addMetric(tagValues, value);
                metricFamilySamples.add(gaugeMetricFamily);
              } else {
                System.out.println("NOT A GAUGE");
              }
            }
          }
        });
    return metricFamilySamples;
  }

  private String metricName(KafkaMetric metric) {
    return String.format("%s.%s", metric.metricName().group(), metric.metricName().name())
        .replace(".", "_")
        .replace("-", "_");
  }
}
