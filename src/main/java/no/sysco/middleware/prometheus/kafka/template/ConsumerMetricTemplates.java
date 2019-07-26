package no.sysco.middleware.prometheus.kafka.template;

import no.sysco.middleware.prometheus.kafka.template.common.CommonTemplates;
import no.sysco.middleware.prometheus.kafka.template.common.KafkaClient;
import no.sysco.middleware.prometheus.kafka.template.common.PerBrokerTemplates;
import no.sysco.middleware.prometheus.kafka.template.consumer.ConsumerFetchTemplates;
import no.sysco.middleware.prometheus.kafka.template.consumer.ConsumerFetchTopicTemplates;
import no.sysco.middleware.prometheus.kafka.template.consumer.ConsumerGroupTemplates;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KeyValue;

import java.util.Set;

/**
 * ConsumerMetricTemplates
 * https://kafka.apache.org/documentation/#consumer_monitoring
 */
public class ConsumerMetricTemplates extends MetricTemplates {
    /**
     * Value of KafkaConsumer.JMX_PREFIX
     * Value is mapped to mBean domain
     */
    public final static String CONSUMER_DOMAIN = "kafka.consumer";

    /**
     * Metric Groups
     * Ref: https://kafka.apache.org/documentation/#selector_monitoring
     */
    public final static String CONSUMER_METRIC_GROUP_NAME = "consumer-metrics";
    public final static String CONSUMER_COORDINATOR_METRIC_GROUP_NAME = "consumer-coordinator-metrics";
    public final static String CONSUMER_FETCH_METRIC_GROUP_NAME = "consumer-fetch-manager-metrics";

    /** common templates */
    public final CommonTemplates commonTemplates;                           // `consumer-metrics`
    public final PerBrokerTemplates perBrokerTemplates;                     // `consumer-node-metrics`

    /** consumer-only templates */
    public final ConsumerGroupTemplates consumerGroupTemplates;             // `consumer-coordinator-metrics`
    public final ConsumerFetchTemplates consumerFetchTemplates;             // `consumer-fetch-manager-metrics`
    public final ConsumerFetchTopicTemplates consumerFetchTopicTemplates;   // `consumer-fetch-manager-metrics`


    public ConsumerMetricTemplates() {
        this.commonTemplates = new CommonTemplates(KafkaClient.CONSUMER);
        this.perBrokerTemplates = new PerBrokerTemplates(KafkaClient.CONSUMER);
        this.consumerGroupTemplates = new ConsumerGroupTemplates();
        this.consumerFetchTemplates = new ConsumerFetchTemplates();
        this.consumerFetchTopicTemplates = new ConsumerFetchTopicTemplates();
    }

    // single client-id
    public Set<MetricName> getMetricNamesCommon(Set<String> clientIdSet) {
        return getMetricNamesPerClientId(clientIdSet, commonTemplates.templates);
    }
    // single client-id
    public Set<MetricName> getMetricNamesConsumerGroup(Set<String> clientIdSet) {
        return getMetricNamesPerClientId(clientIdSet, consumerGroupTemplates.templates);
    }
    // single client-id
    public Set<MetricName> getMetricNamesFetchGroup(Set<String> clientIdSet) {
        return getMetricNamesPerClientId(clientIdSet, consumerFetchTemplates.templates);
    }
    // pair
    public Set<MetricName> getMetricNamesFetchTopicGroup(Set<KeyValue<String, String>> clientIdTopicSet) {
        return perBrokerTemplates.getMetricNames(clientIdTopicSet);
    }
    // pair
    public Set<MetricName> getMetricNamesPerBrokerGroup(Set<KeyValue<String, String>> clientIdNodeSet) {
        return perBrokerTemplates.getMetricNames(clientIdNodeSet);
    }


}
