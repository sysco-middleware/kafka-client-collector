package no.sysco.middleware.prometheus.kafka.template;

import no.sysco.middleware.prometheus.kafka.template.common.CommonTemplates;
import no.sysco.middleware.prometheus.kafka.template.common.KafkaClient;
import no.sysco.middleware.prometheus.kafka.template.common.PerBrokerTemplates;
import no.sysco.middleware.prometheus.kafka.template.producer.ProducerInstanceTemplates;
import no.sysco.middleware.prometheus.kafka.template.producer.ProducerTopicMetricsTemplates;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KeyValue;

import java.util.*;

/**
 * ProducerMetricTemplates
 * https://kafka.apache.org/documentation/#producer_monitoring
 */
public class ProducerMetricTemplates extends MetricTemplates {
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

    /** common templates */
    public final CommonTemplates commonTemplates; // `producer-metrics`
    public final PerBrokerTemplates perBrokerTemplates; // `producer-node-metrics`

    /** producer only templates */
    public final ProducerInstanceTemplates producerInstanceTemplates; // `producer-metrics`
    public final ProducerTopicMetricsTemplates producerTopicMetricsTemplates; // `producer-topic-metrics`

    public ProducerMetricTemplates() {
        this.commonTemplates = new CommonTemplates(KafkaClient.PRODUCER);
        this.perBrokerTemplates = new PerBrokerTemplates(KafkaClient.PRODUCER);
        this.producerInstanceTemplates = new ProducerInstanceTemplates();
        this.producerTopicMetricsTemplates = new ProducerTopicMetricsTemplates();
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
