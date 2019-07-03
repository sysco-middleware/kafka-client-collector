package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.internal.ProducerMetricsTemplates;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KeyValue;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// producer-metrics
// Domains will look like :
// [JMImplementation, java.util.logging, java.lang, com.sun.management, kafka.producer, java.nio]
public class ProducerJmxCollector extends KafkaClientJmxCollector {

    private final Set<MetricName> producerMetricNamesAtStartup;
    private ProducerMetricsTemplates producerMetricsTemplates;

    private ProducerJmxCollector(MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.producerMetricsTemplates = new ProducerMetricsTemplates();
        this.producerMetricNamesAtStartup = initProducerMetricNamesWithClientId();
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


    // kafka.producer:type=producer-topic-metrics,client-id=25862c0e-9da0-48f7-82b5-cdf077d1ff6a,topic=topic-2
    public List<KeyValue<String, String>> getClientTopicList(final String clientId) {
        String objectNameWithDomain =
                ProducerMetricsTemplates.PRODUCER_DOMAIN  +
                        ":type=" + ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME +
                        ",client-id=" + clientId + ",*";
        List<KeyValue<String, String>> clientTopicList = new ArrayList<>();
        try {
            ObjectName mbeanObjectName = new ObjectName(objectNameWithDomain);
            Set<ObjectName> objectNamesFromString = mBeanServer.queryNames(mbeanObjectName, null);
            for (ObjectName objectName : objectNamesFromString) {
                String id = objectName.getKeyProperty("client-id");
                String topicName = objectName.getKeyProperty("topic");
                clientTopicList.add(KeyValue.pair(id, topicName));
            }
            return clientTopicList;
        } catch (MalformedObjectNameException mfe) {
            throw new IllegalArgumentException(mfe.getMessage());
        }
    }

    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        List<Collector.MetricFamilySamples> metricsDefinedAtStart =
                getMetricsPerClientId(ProducerMetricsTemplates.PRODUCER_METRIC_GROUP_NAME, producerMetricNamesAtStartup);

        Set<String> clientIds = getKafkaClientIds(ProducerMetricsTemplates.PRODUCER_METRIC_GROUP_NAME);
        List<KeyValue<String, String>> clientsTopicsList = new ArrayList<>();
        for (String id : clientIds) {
            List<KeyValue<String, String>> topicsPerClient = getClientTopicList(id);
            clientsTopicsList.addAll(topicsPerClient);
        }
        Set<MetricName> metricsPerClientIdTopic  = producerMetricsTemplates.getMetricNamesClientIdTopic(clientsTopicsList);
        List<Collector.MetricFamilySamples> metricsDefinedAtRuntime = getMetricsPerClientIdTopic(ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME, metricsPerClientIdTopic);
        List<Collector.MetricFamilySamples> allMetrics = Stream.concat(metricsDefinedAtStart.stream(), metricsDefinedAtRuntime.stream())
                .collect(Collectors.toList());
        return allMetrics;
    }

}
