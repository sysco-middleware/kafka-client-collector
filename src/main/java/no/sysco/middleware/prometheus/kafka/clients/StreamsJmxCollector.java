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
// [JMImplementation, java.util.logging, kafka.consumer, java.lang, com.sun.management, kafka.streams, kafka.producer, java.nio, kafka.admin.client]
/**
 * Kafka Streams API contains others APIs: producer, consumer, admin-client
 *
 * */
public class StreamsJmxCollector extends KafkaClientJmxCollector {
    // domain
    public static String DOMAIN_NAME = "kafka.streams";
    // types
    private static String STREAM_METRICS_TYPE = "stream-metrics";
    private Set<MetricName> streamMetricNames;

    private StreamsJmxCollector(Set<MetricName> allMetricNames, MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.streamMetricNames = allMetricNames.stream().filter(metric -> STREAM_METRICS_TYPE.equals(metric.group())).collect(Collectors.toSet());
    }

    public StreamsJmxCollector(Set<MetricName> metricNames) {
        this(
                metricNames,
                ManagementFactory.getPlatformMBeanServer(),
                DOMAIN_NAME
        );
    }

    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        return getMetrics(STREAM_METRICS_TYPE, streamMetricNames);
    }
}
