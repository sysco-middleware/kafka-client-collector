package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.internal.ProducerMetricsTemplates;
import org.apache.kafka.common.MetricName;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;

// producer-metrics
// Domains will look like :
// [JMImplementation, java.util.logging, java.lang, com.sun.management, kafka.producer, java.nio]
public class ProducerJmxCollector extends KafkaClientJmxCollector {

    private final Set<MetricName> producerMetricNames;
    private ProducerMetricsTemplates producerMetricsTemplates;

    private ProducerJmxCollector(MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.producerMetricsTemplates = new ProducerMetricsTemplates();
        this.producerMetricNames = initProducerMetricNamesWithClientId();
    }

    public ProducerJmxCollector() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                ProducerMetricsTemplates.PRODUCER_DOMAIN
        );
    }

    private Set<MetricName> initProducerMetricNamesWithClientId() {
        Set<String> kafkaClientIds = getKafkaClientIds(ProducerMetricsTemplates.PRODUCER_METRIC_GROUP_NAME);
        return producerMetricsTemplates.getMetricNamesPerClientId(kafkaClientIds);
    }

    // todo: add tags at runtime. Such as client-id, node-id, etc. . .

    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        return getMetrics(ProducerMetricsTemplates.PRODUCER_METRIC_GROUP_NAME, producerMetricNames);
    }


}
