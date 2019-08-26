package no.sysco.middleware.prometheus.kafka.clients;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.prometheus.client.SummaryMetricFamily;
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
            final List<String> tagsKeys = metric.metricName().tags().keySet().stream().map(this::metricTag).collect(Collectors.toList());
            final List<String> tagValues = new ArrayList<>(metric.metricName().tags().values());

            if (filterBadMetrics(kafkaMetric)) {
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
                  // not counter
                }
              }
              // naming convention
              // https://prometheus.io/docs/concepts/metric_types/#summary
              else if (kafkaMetric.metricName().name().contains("count")) {
                double value;
                if (kafkaMetric.metricValue() instanceof Double) {
                  value = (double) kafkaMetric.metricValue();
                  SummaryMetricFamily summaryMetricFamily =
                          new SummaryMetricFamily(metricName(kafkaMetric), kafkaMetric.metricName().description(), tagsKeys);
                  summaryMetricFamily.addMetric(tagValues, value, value);
                  metricFamilySamples.add(summaryMetricFamily);
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
                  // not a gauge
                }
              }
            }
          }
        });
    return metricFamilySamples;
  }

  private String metricName(KafkaMetric metric) {
    return String.format("%s.%s", metric.metricName().group(), metric.metricName().name())
        .replaceAll("\\.", "_")
        .replaceAll("-", "_");
  }

  private String metricTag(String tagName) {
    return tagName.replaceAll("-", "_");
  }

  private Boolean filterBadMetrics(final KafkaMetric kafkaMetric) {
    // some metrics has no description
    if (kafkaMetric.metricName().description().isEmpty()) {
      return false;
    }
    // append fail https://github.com/sysco-middleware/kafka-client-collector/issues/12
    // kafka node metrics repeats same set twice for `node-id="node-1"` and `node-id="node--1"`
    // todo: investigate
    if (kafkaMetric.metricName().tags().values().stream().anyMatch((tagValue -> tagValue.contains("--")))) {
      return false;
    }
    // The app-info mbean registered with JMX to provide version and commit id will be deprecated and replaced with metrics providing these attributes.
    if ("app-info".equalsIgnoreCase(kafkaMetric.metricName().group()) ||
            "kafka-metrics-count".equalsIgnoreCase(kafkaMetric.metricName().group())) {
      return false;
    }
    return true;
  }

}
