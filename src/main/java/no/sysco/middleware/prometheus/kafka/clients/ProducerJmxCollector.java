package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.internal.MetricNamesUtils;
import no.sysco.middleware.prometheus.kafka.internal.ProducerMetricNames;
import org.apache.kafka.clients.producer.internals.SenderMetricsRegistry;
import org.apache.kafka.common.MetricName;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;

// producer-metrics
// Domains will look like :
// [JMImplementation, java.util.logging, java.lang, com.sun.management, kafka.producer, java.nio]
public class ProducerJmxCollector extends KafkaClientJmxCollector {

    public static String PRODUCER_DOMAIN = "kafka.producer"; // KafkaProducer.JMX_PREFIX
    public static String PRODUCER_METRIC_TYPE = "producer-metrics"; // KafkaProducer.PRODUCER_METRIC_GROUP_NAME;
    private final Set<MetricName> producerMetricNames;

    private ProducerJmxCollector(MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.producerMetricNames = initProducerMetricNamesWithClientId();
    }

    public ProducerJmxCollector() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                PRODUCER_DOMAIN
        );
    }

    // add tags at runtime. Such as client-id, node-id, etc. . .
    private Set<MetricName> initProducerMetricNamesWithClientId() {
        Set<String> kafkaClientIds = getKafkaClientIds(PRODUCER_METRIC_TYPE);
        Set<MetricName> metricNames = MetricNamesUtils.getMetricNamesWithClientId(kafkaClientIds, ProducerMetricNames.INITIAL_METRIC_NAMES);
        return metricNames;
    }


    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        return getMetrics(PRODUCER_METRIC_TYPE, producerMetricNames);
    }


}
