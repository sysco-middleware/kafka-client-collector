package no.sysco.middleware.prometheus.kafka.clients;

import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import org.apache.kafka.common.MetricName;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


// Domains will look like :
// [JMImplementation, java.util.logging, java.lang, com.sun.management, kafka.consumer, java.nio]
public class ConsumerJmxCollector extends KafkaClientJmxCollector {
    // domain
    public static String DOMAIN_NAME = "kafka.consumer";
    // types
    private static String CONSUMER_METRICS_TYPE = "consumer-metrics";
    private static String CONSUMER_FETCH_MANAGER_METRICS_TYPE = "consumer-fetch-manager-metrics";
    private static String CONSUMER_COORDINATOR_METRICS_TYPE = "consumer-coordinator-metrics";

    private Set<MetricName> consumerMetricNames;
    private Set<MetricName> consumerFetchManagerMetricNames;
    private Set<MetricName> consumerCoordinatorMetricNames;

    private ConsumerJmxCollector(Set<MetricName> allMetricNames, MBeanServer mBeanServer, String domainName) {
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

}
