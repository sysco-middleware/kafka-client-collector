package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import no.sysco.middleware.prometheus.kafka.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.template.ConsumerMetricTemplates;
import no.sysco.middleware.prometheus.kafka.template.ProducerMetricTemplates;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KeyValue;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Kafka-Consumer register 5 types of jmx metrics in one `kafka.consumer` domain.
 * ref: https://kafka.apache.org/documentation/#consumer_monitoring
 * <p>
 * 1. `app-info` - metric group name is going to be deprecated.
 * This metric group does not supported by current implementation.
 * 2. `consumer-metrics` - common metrics.
 * kafka.[producer|consumer|connect]:type=[consumer|producer|connect]-metrics,client-id=([-.\w]+)
 * 3. `consumer-node-metric` - metrics is per broker(per node). Common metric for all clients.
 * kafka.[producer|consumer|connect]:type=[consumer|producer|connect]-node-metrics,client-id=([-.\w]+),node-id=([0-9]+)
 * 4. `consumer-coordinator-metrics` - consumer group metrics
 * kafka.consumer:type=consumer-coordinator-metrics,client-id=([-.\w]+)
 * 5. `consumer-fetch-manager-metrics` - metrics per client, and per topic, and per partition.
 * kafka.consumer:type=consumer-fetch-manager-metrics,client-id="{client-id}"
 * kafka.consumer:type=consumer-fetch-manager-metrics,client-id="{client-id}",topic="{topic}"
 * kafka.consumer:type=consumer-fetch-manager-metrics,partition="{partition}",topic="{topic}",client-id="{client-id}"
 */
public class ConsumerJmxCollector extends KafkaClientJmxCollector {

    private ConsumerMetricTemplates consumerMetricTemplates;
    private Set<String> kafkaClientIds;

    private ConsumerJmxCollector(MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.consumerMetricTemplates = new ConsumerMetricTemplates();
        this.kafkaClientIds = getKafkaClientIds(ConsumerMetricTemplates.CONSUMER_METRIC_GROUP_NAME);
    }

    public ConsumerJmxCollector() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                ConsumerMetricTemplates.CONSUMER_DOMAIN
        );
    }

    List<Collector.MetricFamilySamples> getMetricsTopic() {
        // producer-topic-metrics
        String metricType = consumerMetricTemplates.consumerFetchTopicTemplates.metricGroupName;
        Set<KeyValue<String, String>> clientsTopicsList = new HashSet<>();
        for (String id : kafkaClientIds) {
            Set<KeyValue<String, String>> topicsPerClient = getClientTopicSet(ConsumerMetricTemplates.CONSUMER_DOMAIN, metricType, id);
            clientsTopicsList.addAll(topicsPerClient);
        }
        Set<MetricName> metricsPerClientIdTopic = consumerMetricTemplates.getMetricNamesFetchTopicGroup(clientsTopicsList);
        List<Collector.MetricFamilySamples> perClientIdTopic = getMetricsPerClientIdTopic(metricType, metricsPerClientIdTopic);
        return perClientIdTopic;
    }

    List<Collector.MetricFamilySamples> getMetricsNode() {
        // consumer-node-metrics
        String metricType = consumerMetricTemplates.perBrokerTemplates.metricGroupName;
        Set<KeyValue<String, String>> clientsBrokerSet = new HashSet<>();
        for (String id : kafkaClientIds) {
            Set<KeyValue<String, String>> brokersPerClient = getClientNodeSet(ConsumerMetricTemplates.CONSUMER_DOMAIN, metricType, id);
            clientsBrokerSet.addAll(brokersPerClient);
        }
        Set<MetricName> metricNamePerBrokerSet = consumerMetricTemplates.getMetricNamesPerBrokerGroup(clientsBrokerSet);
        List<Collector.MetricFamilySamples> metricsPerBroker = getMetricsPerBroker(metricType, metricNamePerBrokerSet);
        return metricsPerBroker;
    }


    @Override
    public List<Collector.MetricFamilySamples> getAllMetrics() {
        Set<MetricName> metricNamesCommon = consumerMetricTemplates.getMetricNamesCommon(kafkaClientIds);
        Set<MetricName> metricNamesConsumerGroup = consumerMetricTemplates.getMetricNamesConsumerGroup(kafkaClientIds);
        Set<MetricName> metricNamesFetchGroup = consumerMetricTemplates.getMetricNamesFetchGroup(kafkaClientIds);

        // consumer-metrics (common)
        List<Collector.MetricFamilySamples> metricsCommon =
                getMetricsPerClient(ConsumerMetricTemplates.CONSUMER_METRIC_GROUP_NAME, metricNamesCommon);
        // consumer-coordinator-metrics (consumer group)
        List<Collector.MetricFamilySamples> metricsConsumerGroup =
                getMetricsPerClient(ConsumerMetricTemplates.CONSUMER_COORDINATOR_METRIC_GROUP_NAME,metricNamesConsumerGroup);
        // consumer-node-metrics
        List<Collector.MetricFamilySamples> metricsPerNode = getMetricsNode();

        // consumer-fetch-manager-metrics (common fetch manager metrics)
        List<Collector.MetricFamilySamples> metricsFetchGroup =
                getMetricsPerClient(ConsumerMetricTemplates.CONSUMER_FETCH_METRIC_GROUP_NAME, metricNamesFetchGroup);
        // consumer-fetch-manager-metrics (per topic fetch manager metrics)
//        List<Collector.MetricFamilySamples> metricsPerTopic = getMetricsTopic();


        return Stream
                .of(metricsCommon, metricsConsumerGroup, metricsPerNode, metricsFetchGroup)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }



}
