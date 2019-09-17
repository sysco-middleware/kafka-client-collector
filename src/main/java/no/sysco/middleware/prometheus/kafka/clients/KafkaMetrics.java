package no.sysco.middleware.prometheus.kafka.clients;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import java.util.ArrayList;
import java.util.HashMap;
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
    Map<MetricName, ? extends Metric> metrics = metricsSupplier.get();
    Map<String, Collector.MetricFamilySamples> prometheusFotmattingMetrics = new HashMap<>();

    metrics.forEach(
        (metricName, metric) -> {
          if (metric instanceof KafkaMetric) {
            KafkaMetric kafkaMetric = (KafkaMetric) metric;
            String metricNamePrometheus = metricName(kafkaMetric);
            final List<String> tagsKeys = metric.metricName().tags().keySet().stream().map(this::metricTag).collect(Collectors.toList());
            final List<String> tagValues = new ArrayList<>(metric.metricName().tags().values());

            if (filterBadMetrics(kafkaMetric)) {
              if (kafkaMetric.metricName().name().endsWith("total")) {
                double value;
                if (metric.metricValue() instanceof Double) {
                  value = (double) metric.metricValue();

                  if (prometheusFotmattingMetrics.containsKey(metricNamePrometheus)) {
                    // already registered once
                    // find
                    // add new tags & values
                    CounterMetricFamily counterMetricFamily = (CounterMetricFamily) prometheusFotmattingMetrics.get(metricNamePrometheus);
                    counterMetricFamily.addMetric(tagValues, value);
                  } else {
                    // never registered
                    CounterMetricFamily counterMetricFamily = new CounterMetricFamily(metricNamePrometheus, kafkaMetric.metricName().description(), tagsKeys);
                    counterMetricFamily.addMetric(tagValues, value);
                    prometheusFotmattingMetrics.put(metricNamePrometheus, counterMetricFamily);
                  }

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

                  if (prometheusFotmattingMetrics.containsKey(metricNamePrometheus)) {
                    SummaryMetricFamily summaryMetricFamily = (SummaryMetricFamily) prometheusFotmattingMetrics.get(metricNamePrometheus);
                    summaryMetricFamily.addMetric(tagValues, value, value);
                  } else {
                    SummaryMetricFamily summaryMetricFamily =
                            new SummaryMetricFamily(metricNamePrometheus, kafkaMetric.metricName().description(), tagsKeys);
                    summaryMetricFamily.addMetric(tagValues, value, value);
                    prometheusFotmattingMetrics.put(metricNamePrometheus, summaryMetricFamily);
                  }
                } else {
                  // not a summary
                }
              } else {
                double value;
                if (metric.metricValue() instanceof Double) {
                  value = (double) metric.metricValue();

                  if (prometheusFotmattingMetrics.containsKey(metricNamePrometheus)) {
                    GaugeMetricFamily gaugeMetricFamily = (GaugeMetricFamily) prometheusFotmattingMetrics.get(metricNamePrometheus);
                    gaugeMetricFamily.addMetric(tagValues, value);
                  } else {
                    GaugeMetricFamily gaugeMetricFamily =
                            new GaugeMetricFamily(metricNamePrometheus, kafkaMetric.metricName().description(), tagsKeys);
                    gaugeMetricFamily.addMetric(tagValues, value);
                    prometheusFotmattingMetrics.put(metricNamePrometheus, gaugeMetricFamily);
                  }
                } else {
                  // not a gauge
                }
              }
            }
          }
        });

    return new ArrayList<>(prometheusFotmattingMetrics.values());
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
