package no.sysco.middleware.prometheus.kafka.template;

import no.sysco.middleware.prometheus.kafka.template.common.CommonTemplates;
import no.sysco.middleware.prometheus.kafka.template.common.KafkaClient;
import no.sysco.middleware.prometheus.kafka.template.common.PerBrokerTemplates;
import no.sysco.middleware.prometheus.kafka.template.producer.ProducerInstanceTemplates;
import no.sysco.middleware.prometheus.kafka.template.producer.ProducerTopicMetricsTemplates;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;
import org.apache.kafka.streams.KeyValue;

import java.util.*;

/**
 * ProducerMetricTemplates has 2 group of metrics
 *
 * https://kafka.apache.org/documentation/#producer_monitoring
 *
 * 1. `producer-metrics` - common producer sender metrics.
 * kafka.producer:type=producer-metrics,client-id="{client-id}"
 * 2. `producer-topic-metrics` - metrics per topic.
 * kafka.producer:type=producer-topic-metrics,client-id="{client-id}",topic="{topic}"
 */
public class ProducerMetricTemplates {
    /**
     * MBean domain.
     * Value taken from KafkaProducer.JMX_PREFIX
     */
    public final static String PRODUCER_DOMAIN = "kafka.producer";
    /**
     * Metric Groups
     * Ref: https://kafka.apache.org/documentation/#selector_monitoring
     */
    public final static String PRODUCER_METRIC_GROUP_NAME = "producer-metrics"; // KafkaProducer.PRODUCER_METRIC_GROUP_NAME
    public final static String PRODUCER_TOPIC_METRIC_GROUP_NAME = "producer-topic-metrics"; // SenderMetricsRegistry.TOPIC_METRIC_GROUP_NAME

    /** templates */
    // common templates
    public final CommonTemplates commonTemplates;
    public final PerBrokerTemplates perBrokerTemplates;
    // producer only templates
    public final ProducerInstanceTemplates producerInstanceTemplates;
    public final ProducerTopicMetricsTemplates producerTopicMetricsTemplates;


    public ProducerMetricTemplates() {
        this.commonTemplates = new CommonTemplates(KafkaClient.PRODUCER);
        this.perBrokerTemplates = new PerBrokerTemplates(KafkaClient.PRODUCER);
        this.producerInstanceTemplates = new ProducerInstanceTemplates();
        this.producerTopicMetricsTemplates = new ProducerTopicMetricsTemplates();
    }


    /**
     * Get a subset of MetricName per clientId
     *
     * Subset initialised at startup of application
     */
    public Set<MetricName> getMetricNamesPerClientId(Set<String> clientIdSet, Set<MetricNameTemplate> metricNameTemplates) {
        Set<MetricName> metricNames = new HashSet<>();
        for (String clientId : clientIdSet) {
            for (MetricNameTemplate metricName : metricNameTemplates) {
                metricNames.add(
                        new MetricName(
                                metricName.name(),
                                metricName.group(),
                                metricName.description(),
                                new HashMap<String, String>() {{
                                    put("client-id", clientId);
                                }}
                        )
                );
            }
        }
        return metricNames;
    }

    // single client-id
    public Set<MetricName> getMetricNamesProducerInstance(Set<String> clientIdSet) {
        return getMetricNamesPerClientId(clientIdSet, producerInstanceTemplates.templates);
    }
    // single client-id
    public Set<MetricName> getMetricNamesCommon(Set<String> clientIdSet) {
        return getMetricNamesPerClientId(clientIdSet, commonTemplates.templates);
    }

    // pair
    public Set<MetricName> getMetricNamesProducerTopicGroup(Set<KeyValue<String, String>> clientIdTopicSet){
        return producerTopicMetricsTemplates.getMetricNamesProducerTopicGroup(clientIdTopicSet);
    }
    // pair
    public Set<MetricName> getMetricNamesPerBrokerGroup(Set<KeyValue<String, String>> clientIdNodeSet) {
        return perBrokerTemplates.getMetricNamesPerBrokerGroup(clientIdNodeSet);
    }
}
