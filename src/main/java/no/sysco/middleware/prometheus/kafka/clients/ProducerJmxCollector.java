package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.template.ProducerMetricTemplates;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KeyValue;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Kafka-Producer register 4 types of jmx metrics in one `kafka.producer` domain.
 * ref: https://kafka.apache.org/documentation/#producer_monitoring
 * <p>
 * 1. `app-info` - metric group name is going to be deprecated.
 * This metric group does not supported by current implementation.
 * 2. `producer-metrics` - common metrics and producer sender metrics.
 * kafka.[producer|consumer|connect]:type=[consumer|producer|connect]-metrics,client-id=([-.\w]+)
 * 3. `producer-node-metric` - metrics is per broker. Common metric for all clients.
 * kafka.[producer|consumer|connect]:type=[consumer|producer|connect]-node-metrics,client-id=([-.\w]+),node-id=([0-9]+)
 * 4. `producer-topic-metrics` - metrics per topic.
 * kafka.producer:type=producer-topic-metrics,client-id="{client-id}",topic="{topic}"
 */
public class ProducerJmxCollector extends KafkaClientJmxCollector {
    private ProducerMetricTemplates producerMetricTemplates;
    private Set<String> kafkaClientIds;

    private ProducerJmxCollector(MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.producerMetricTemplates = new ProducerMetricTemplates();
        this.kafkaClientIds = getKafkaClientIds(ProducerMetricTemplates.PRODUCER_METRIC_GROUP_NAME);
    }

    public ProducerJmxCollector() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                ProducerMetricTemplates.PRODUCER_DOMAIN
        );
    }

    List<Collector.MetricFamilySamples> getMetricsTopic() {
        // producer-topic-metrics
        String metricType = producerMetricTemplates.producerTopicMetricsTemplates.metricGroupName;
        Set<KeyValue<String, String>> clientsTopicsList = new HashSet<>();
        for (String id : kafkaClientIds) {
            Set<KeyValue<String, String>> topicsPerClient = getClientTopicSet(ProducerMetricTemplates.PRODUCER_DOMAIN, metricType, id);
            clientsTopicsList.addAll(topicsPerClient);
        }
        Set<MetricName> metricsPerClientIdTopic = producerMetricTemplates.getMetricNamesProducerTopicGroup(clientsTopicsList);
        List<Collector.MetricFamilySamples> perClientIdTopic = getMetricsPerClientIdTopic(metricType, metricsPerClientIdTopic);
        return perClientIdTopic;
    }

    List<Collector.MetricFamilySamples> getMetricsNode() {
        // producer-node-metrics
        String metricType = producerMetricTemplates.perBrokerTemplates.metricGroupName;
        Set<String> clientIds = getKafkaClientIds(metricType);
        Set<KeyValue<String, String>> clientsBrokerSet = new HashSet<>();
        for (String id : clientIds) {
            Set<KeyValue<String, String>> brokersPerClient = getClientNodeSet(ProducerMetricTemplates.PRODUCER_DOMAIN, metricType, id);
            clientsBrokerSet.addAll(brokersPerClient);
        }
        Set<MetricName> metricNamePerBrokerSet = producerMetricTemplates.getMetricNamesPerBrokerGroup(clientsBrokerSet);
        List<Collector.MetricFamilySamples> metricsPerBroker = getMetricsPerBroker(metricType, metricNamePerBrokerSet);
        return metricsPerBroker;
    }

    @Override
    public List<Collector.MetricFamilySamples> getAllMetrics() {
        Set<MetricName> metricNamesCommon = producerMetricTemplates.getMetricNamesCommon(kafkaClientIds);
        Set<MetricName> metricNamesProducerInstance = producerMetricTemplates.getMetricNamesProducerInstance(kafkaClientIds);
        // producer-metrics (common)
        List<Collector.MetricFamilySamples> metricsCommon =
                getMetricsPerClient(ProducerMetricTemplates.PRODUCER_METRIC_GROUP_NAME, metricNamesCommon);
        // producer-metrics (producer instance metrics)
        List<Collector.MetricFamilySamples> metricsInstanceProducer =
                getMetricsPerClient(ProducerMetricTemplates.PRODUCER_METRIC_GROUP_NAME, metricNamesProducerInstance);
        // producer-node-metrics
        List<Collector.MetricFamilySamples> metricsPerNode = getMetricsNode();
        // producer-topic-metrics
        List<Collector.MetricFamilySamples> metricsPerTopic = getMetricsTopic();

        return Stream
                .of(metricsCommon, metricsInstanceProducer, metricsPerNode, metricsPerTopic)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
