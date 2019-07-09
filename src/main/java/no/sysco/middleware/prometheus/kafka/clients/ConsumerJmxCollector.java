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
 * 2. `consumer-metrics` - common producer sender metrics.
 * kafka.consumer:type=consumer-metrics,client-id="{client-id}"
 * 3. `consumer-node-metric` - metrics is per broker. Common metric for all clients.
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

    List<Collector.MetricFamilySamples> getMetricsPerBroker() {
        // consumer-node-metrics
        String perBrokerMetricGroupName = consumerMetricTemplates.perBrokerTemplates.metricGroupName;
        Set<String> clientIds = getKafkaClientIds(perBrokerMetricGroupName);
        Set<KeyValue<String, String>> clientsBrokerSet = new HashSet<>();
        for (String id : clientIds) {
            Set<KeyValue<String, String>> brokersPerClient = getClientNodeSet(id);
            clientsBrokerSet.addAll(brokersPerClient);
        }
        Set<MetricName> metricNamePerBrokerSet = consumerMetricTemplates.getMetricNamesPerBrokerGroup(clientsBrokerSet);
        List<Collector.MetricFamilySamples> metricsPerBroker = getMetricsPerBroker(perBrokerMetricGroupName, metricNamePerBrokerSet);
        return metricsPerBroker;
    }

    /**
     * Producer client could write to several topics.
     * Examples:
     * kafka.consumer:type=consumer-topic-metrics,client-id=A,node-id=node--1
     * kafka.consumer:type=consumer-topic-metrics,client-id=B,node-id=node--2
     * kafka.consumer:type=consumer-topic-metrics,client-id=A,node-id=node--2
     *
     * {A, node--1},{B, node--2},{A, node--2}
     */
    public Set<KeyValue<String, String>> getClientNodeSet(final String clientId) {
        String objectNameWithDomain =
                ConsumerMetricTemplates.CONSUMER_DOMAIN +
                        ":type=" + consumerMetricTemplates.perBrokerTemplates.metricGroupName +
                        ",client-id=" + clientId + ",*";
        Set<KeyValue<String, String>> clientNodeList = new HashSet<>();
        try {
            ObjectName mbeanObjectName = new ObjectName(objectNameWithDomain);
            Set<ObjectName> objectNamesFromString = mBeanServer.queryNames(mbeanObjectName, null);
            for (ObjectName objectName : objectNamesFromString) {
                String id = objectName.getKeyProperty("client-id");
                String nodeId = objectName.getKeyProperty("node-id");
                clientNodeList.add(KeyValue.pair(id, nodeId));
            }
            return clientNodeList;
        } catch (MalformedObjectNameException mfe) {
            throw new IllegalArgumentException(mfe.getMessage());
        }
    }

    @Override
    public List<Collector.MetricFamilySamples> getAllMetrics() {

        // common
        List<Collector.MetricFamilySamples> metricsCommon = getMetricsPerClient(
                ConsumerMetricTemplates.CONSUMER_METRIC_GROUP_NAME,
                consumerMetricTemplates.getMetricNamesCommon(kafkaClientIds));

        // consumer group
        List<Collector.MetricFamilySamples> metricsConsumerGroup = getMetricsPerClient(
                ConsumerMetricTemplates.CONSUMER_COORDINATOR_METRIC_GROUP_NAME,
                consumerMetricTemplates.getMetricNamesConsumerGroup(kafkaClientIds));

        // consumer-node-metrics
        List<Collector.MetricFamilySamples> metricsPerBroker = getMetricsPerBroker();

        return Stream
                .of(metricsCommon, metricsConsumerGroup, metricsPerBroker)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }



}
