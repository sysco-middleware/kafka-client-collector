package no.sysco.middleware.prometheus.kafka.clients;

import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import org.apache.kafka.common.MetricName;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Domains will look like :
// [JMImplementation, java.util.logging, java.lang, com.sun.management, kafka.admin.client, java.nio]
public class AdminClientJmxCollector extends KafkaClientJmxCollector {

    public static String DOMAIN_NAME = "kafka.admin.client";
    private static String ADMIN_CLIENT_METRIC_TYPE = "admin-client-metrics";
//    private static String ADMIN_CLIENT_NODE_METRIC_TYPE = "admin-client-node-metrics"; //todo:

    private Set<MetricName> adminClientMetricNames;

    private AdminClientJmxCollector(Set<MetricName> allMetricNames, MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.adminClientMetricNames = allMetricNames.stream().filter(metric -> ADMIN_CLIENT_METRIC_TYPE.equals(metric.group())).collect(Collectors.toSet());
    }

    public AdminClientJmxCollector(Set<MetricName> allMetricNames) {
        this(
                allMetricNames,
                ManagementFactory.getPlatformMBeanServer(),
                DOMAIN_NAME
        );
    }

    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        return getMetrics(ADMIN_CLIENT_METRIC_TYPE, adminClientMetricNames);
    }
}
