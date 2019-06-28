package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
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

// producer-metrics
public class ProducerJmxCollector extends KafkaClientJmxCollector {
    public static String DOMAIN_NAME = "kafka.producer";
    public static String PRODUCER_METRIC_TYPE = "producer-metrics";
    private Set<MetricName> metricNames;

    private ProducerJmxCollector(Set<MetricName> allMetricNames, MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.metricNames = allMetricNames.stream().filter(metric -> PRODUCER_METRIC_TYPE.equals(metric.group())).collect(Collectors.toSet());
    }

    public ProducerJmxCollector(Set<MetricName> metricNames) {
        this(
                metricNames,
                ManagementFactory.getPlatformMBeanServer(),
                DOMAIN_NAME
        );
    }

    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        return getMetrics(PRODUCER_METRIC_TYPE, metricNames);
    }


}
