package no.sysco.middleware.prometheus.kafka.clients;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import org.apache.kafka.common.MetricName;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static no.sysco.middleware.prometheus.kafka.clients.ProducerJmxCollector.METRIC_TYPE;

// consumer-metrics
// consumer-fetch-manager-metrics
// consumer-coordinator-metrics
public class ConsumerJmxCollector extends KafkaClientJmxCollector {
    // domain
    public static String DOMAIN_NAME = "kafka.consumer";
    // types
    public static String CONSUMER_METRICS_TYPE = "consumer-metrics";
    public static String CONSUMER_FETCH_MANAGER_METRICS_TYPE = "consumer-fetch-manager-metrics";
    public static String CONSUMER_COORDINATOR_METRICS_TYPE = "consumer-coordinator-metrics";

    private Set<MetricName> consumerMetricNames;
    private Set<MetricName> consumerFetchManagerMetricNames;
    private Set<MetricName> consumerCoordinatorMetricNames;

    public ConsumerJmxCollector(Set<MetricName> allMetricNames, MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.consumerMetricNames = allMetricNames.stream().filter(metric -> CONSUMER_METRICS_TYPE.equals(metric.group())).collect(Collectors.toSet());
        this.consumerFetchManagerMetricNames = allMetricNames.stream().filter(metric -> CONSUMER_FETCH_MANAGER_METRICS_TYPE.equals(metric.group())).collect(Collectors.toSet());
        this.consumerCoordinatorMetricNames = allMetricNames.stream().filter(metric -> CONSUMER_COORDINATOR_METRICS_TYPE.equals(metric.group())).collect(Collectors.toSet());
    }

    public ConsumerJmxCollector(Set<MetricName> allMetricNames) {
        this(
                allMetricNames,
                ManagementFactory.getPlatformMBeanServer(),
                DOMAIN_NAME
        );
    }

    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        List<Collector.MetricFamilySamples> mfs = new ArrayList<>();
        mfs.addAll(getMetrics(CONSUMER_METRICS_TYPE, consumerMetricNames));
        mfs.addAll(getMetrics(CONSUMER_FETCH_MANAGER_METRICS_TYPE, consumerFetchManagerMetricNames));
        mfs.addAll(getMetrics(CONSUMER_COORDINATOR_METRICS_TYPE, consumerCoordinatorMetricNames));
        return mfs;
    }

    public List<Collector.MetricFamilySamples> getMetrics(final String metricType, final Set<MetricName> metricNames) {
        List<Collector.MetricFamilySamples> metricFamilySamples = new ArrayList<>();
        for (MetricName metricName : metricNames) {
            String id = metricName.tags().get("client-id");
            if (metricType.contains(metricName.group())) {
                if (metricName.name().contains("-total")){
                    CounterMetricFamily counterMetricFamily = new CounterMetricFamily(
                            formatMetricName(metricName),
                            metricName.description(),
                            Collections.singletonList("id")
                    );
                    counterMetricFamily.addMetric(
                            Collections.singletonList(id),
                            getMBeanAttributeValue(metricType, metricName.name(), id, Long.class)
                    );
                    metricFamilySamples.add(counterMetricFamily);
                } else {
                    GaugeMetricFamily gaugeMetricFamily = new GaugeMetricFamily(
                            formatMetricName(metricName),
                            metricName.description(),
                            Collections.singletonList("id")
                    );
                    gaugeMetricFamily.addMetric(
                            Collections.singletonList(id),
                            getMBeanAttributeValue(metricType, metricName.name(), id, Double.class)
                    );
                    metricFamilySamples.add(gaugeMetricFamily);
                }

            }
        }
        return metricFamilySamples;
    }

}
