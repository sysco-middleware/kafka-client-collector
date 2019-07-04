package no.sysco.middleware.prometheus.kafka.template;

import no.sysco.middleware.prometheus.kafka.template.common.KafkaClient;
import no.sysco.middleware.prometheus.kafka.template.common.PerBrokerMetricTemplate;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;
import org.apache.kafka.streams.KeyValue;

import java.util.*;
import java.util.stream.Collectors;

public class ProducerMetricsTemplates {

    private final PerBrokerMetricTemplate perBrokerMetricTemplate;

    /**
     * Value of KafkaProducer.JMX_PREFIX
     * <p>
     * Value is mapped to mBean domain
     */
    public final static String PRODUCER_DOMAIN = "kafka.producer"; //

    /**
     * Value of KafkaProducer.PRODUCER_METRIC_GROUP_NAME
     * <p>
     * This type of metrics initialised at startup of application,
     * when producers have already been initialized
     * <p>
     * Ref: https://kafka.apache.org/documentation/#selector_monitoring
     */
    public final static String PRODUCER_METRIC_GROUP_NAME = "producer-metrics";

    /**
     * Value of SenderMetricsRegistry.TOPIC_METRIC_GROUP_NAME
     * <p>
     * This type of metrics initialised at startup of application,
     * when producers have already been initialized
     */
    public final static String PRODUCER_TOPIC_METRIC_GROUP_NAME = "producer-topic-metrics";

    /**
     * Producer Sender Metrics https://kafka.apache.org/documentation/#producer_sender_monitoring
     */

    private final Set<MetricNameTemplate> producerMetricsTemplates;
    private final Set<MetricNameTemplate> producerTopicMetricsTemplates;

    // per clientId
    private final MetricNameTemplate batchSizeAvg;
    private final MetricNameTemplate batchSizeMax;
    private final MetricNameTemplate compressionRateAvg;
    private final MetricNameTemplate recordQueueTimeAvg;
    private final MetricNameTemplate recordQueueTimeMax;
    private final MetricNameTemplate requestLatencyAvg;
    private final MetricNameTemplate requestLatencyMax;
    private final MetricNameTemplate produceThrottleTimeAvg;
    private final MetricNameTemplate produceThrottleTimeMax;
    private final MetricNameTemplate recordSendRate;
    private final MetricNameTemplate recordSendTotal;
    private final MetricNameTemplate recordsPerRequestAvg;
    private final MetricNameTemplate recordRetryRate;
    private final MetricNameTemplate recordRetryTotal;
    private final MetricNameTemplate recordErrorRate;
    private final MetricNameTemplate recordErrorTotal;
    private final MetricNameTemplate recordSizeMax;
    private final MetricNameTemplate recordSizeAvg;
    private final MetricNameTemplate requestsInFlight;
    private final MetricNameTemplate metadataAge;
    private final MetricNameTemplate batchSplitRate;
    private final MetricNameTemplate batchSplitTotal;

    // per pair { clientId : topic }
    private final MetricNameTemplate topicRecordSendRate;
    private final MetricNameTemplate topicRecordSendTotal;
    private final MetricNameTemplate topicByteRate;
    private final MetricNameTemplate topicByteTotal;
    private final MetricNameTemplate topicCompressionRate;
    private final MetricNameTemplate topicRecordRetryRate;
    private final MetricNameTemplate topicRecordRetryTotal;
    private final MetricNameTemplate topicRecordErrorRate;
    private final MetricNameTemplate topicRecordErrorTotal;

    private final Set<String> clientTags; // client-id
    private final Set<String> topicTags; // client-id, topic

    public ProducerMetricsTemplates() {
        this.perBrokerMetricTemplate = new PerBrokerMetricTemplate(KafkaClient.PRODUCER);
        this.clientTags = new HashSet<>(Collections.singletonList("client-id"));
        this.topicTags = new HashSet<>(Arrays.asList("client-id", "topic"));
        this.producerMetricsTemplates = new HashSet<>();
        this.producerTopicMetricsTemplates = new HashSet<>();

        /**
         * Client level
         * at start time
         * */
        this.batchSizeAvg = createTemplate("batch-size-avg", PRODUCER_METRIC_GROUP_NAME, "The average number of bytes sent per partition per-request.", clientTags);
        this.batchSizeMax = createTemplate("batch-size-max", PRODUCER_METRIC_GROUP_NAME, "The max number of bytes sent per partition per-request.", clientTags);
        this.compressionRateAvg = createTemplate("compression-rate-avg", PRODUCER_METRIC_GROUP_NAME, "The average compression rate of record batches.", clientTags);
        this.recordQueueTimeAvg = createTemplate("record-queue-time-avg", PRODUCER_METRIC_GROUP_NAME, "The average time in ms record batches spent in the send buffer.", clientTags);
        this.recordQueueTimeMax = createTemplate("record-queue-time-max", PRODUCER_METRIC_GROUP_NAME, "The maximum time in ms record batches spent in the send buffer.", clientTags);
        this.requestLatencyAvg = createTemplate("request-latency-avg", PRODUCER_METRIC_GROUP_NAME, "The average request latency in ms", clientTags);
        this.requestLatencyMax = createTemplate("request-latency-max", PRODUCER_METRIC_GROUP_NAME, "The maximum request latency in ms", clientTags);
        this.recordSendRate = createTemplate("record-send-rate", PRODUCER_METRIC_GROUP_NAME, "The average number of records sent per second.", clientTags);
        this.recordSendTotal = createTemplate("record-send-total", PRODUCER_METRIC_GROUP_NAME, "The total number of records sent.", clientTags);
        this.recordsPerRequestAvg = createTemplate("records-per-request-avg", PRODUCER_METRIC_GROUP_NAME, "The average number of records per request.", clientTags);
        this.recordRetryRate = createTemplate("record-retry-rate", PRODUCER_METRIC_GROUP_NAME, "The average per-second number of retried record sends", clientTags);
        this.recordRetryTotal = createTemplate("record-retry-total", PRODUCER_METRIC_GROUP_NAME, "The total number of retried record sends", clientTags);
        this.recordErrorRate = createTemplate("record-error-rate", PRODUCER_METRIC_GROUP_NAME, "The average per-second number of record sends that resulted in errors", clientTags);
        this.recordErrorTotal = createTemplate("record-error-total", PRODUCER_METRIC_GROUP_NAME, "The total number of record sends that resulted in errors", clientTags);
        this.recordSizeMax = createTemplate("record-size-max", PRODUCER_METRIC_GROUP_NAME, "The maximum record size", clientTags);
        this.recordSizeAvg = createTemplate("record-size-avg", PRODUCER_METRIC_GROUP_NAME, "The average record size", clientTags);
        this.requestsInFlight = createTemplate("requests-in-flight", PRODUCER_METRIC_GROUP_NAME, "The current number of in-flight requests awaiting a response.", clientTags);
        this.metadataAge = createTemplate("metadata-age", PRODUCER_METRIC_GROUP_NAME, "The age in seconds of the current producer metadata being used.", clientTags);
        this.batchSplitRate = createTemplate("batch-split-rate", PRODUCER_METRIC_GROUP_NAME, "The average number of batch splits per second", clientTags);
        this.batchSplitTotal = createTemplate("batch-split-total", PRODUCER_METRIC_GROUP_NAME, "The total number of batch splits", clientTags);
        this.produceThrottleTimeAvg = createTemplate("produce-throttle-time-avg", PRODUCER_METRIC_GROUP_NAME, "The average time in ms a request was throttled by a broker", clientTags);
        this.produceThrottleTimeMax = createTemplate("produce-throttle-time-max", PRODUCER_METRIC_GROUP_NAME, "The maximum time in ms a request was throttled by a broker", clientTags);

        /***** Topic level *****/
        /****** at run time when producer start send data ******/

        // We can't create the MetricName up front for these, because we don't know the topic name yet.
        this.topicRecordSendRate = createTemplate("record-send-rate", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The average number of records sent per second for a topic.", topicTags);
        this.topicRecordSendTotal = createTemplate("record-send-total", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The total number of records sent for a topic.", topicTags);
        this.topicByteRate = createTemplate("byte-rate", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The average number of bytes sent per second for a topic.", topicTags);
        this.topicByteTotal = createTemplate("byte-total", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The total number of bytes sent for a topic.", topicTags);
        this.topicCompressionRate = createTemplate("compression-rate", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The average compression rate of record batches for a topic.", topicTags);
        this.topicRecordRetryRate = createTemplate("record-retry-rate", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The average per-second number of retried record sends for a topic", topicTags);
        this.topicRecordRetryTotal = createTemplate("record-retry-total", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The total number of retried record sends for a topic", topicTags);
        this.topicRecordErrorRate = createTemplate("record-error-rate", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The average per-second number of record sends that resulted in errors for a topic", topicTags);
        this.topicRecordErrorTotal = createTemplate("record-error-total", PRODUCER_TOPIC_METRIC_GROUP_NAME, "The total number of record sends that resulted in errors for a topic", topicTags);
    }

    private MetricNameTemplate createTemplate(String name, String metricGroupName, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        if (PRODUCER_METRIC_GROUP_NAME.equals(metricGroupName)) {
            producerMetricsTemplates.add(metricNameTemplate);
        } else if (PRODUCER_TOPIC_METRIC_GROUP_NAME.equals(metricGroupName)) {
            producerTopicMetricsTemplates.add(metricNameTemplate);
        } else {
            throw new IllegalArgumentException("Unknown metric group " + metricGroupName);
        }
        return metricNameTemplate;
    }


    // at startup
    public Set<MetricNameTemplate> getProducerGroup() {
        return producerMetricsTemplates.stream().filter(template -> PRODUCER_METRIC_GROUP_NAME.equals(template.group())).collect(Collectors.toSet());
    }

    // dynamic at runtime
    public Set<MetricNameTemplate> getProducerTopicGroup() {
        return producerMetricsTemplates.stream().filter(template -> PRODUCER_TOPIC_METRIC_GROUP_NAME.equals(template.group())).collect(Collectors.toSet());
    }

    /**
     * Get a subset of MetricName per clientId
     *
     * Subset initialised at startup of application
     */
    public Set<MetricName> getMetricNamesProducerGroup(Set<String> clientIdSet) {
        Set<MetricName> metricNames = new HashSet<>();
        for (String clientId : clientIdSet) {
            for (MetricNameTemplate metricName : producerMetricsTemplates) {
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


    /**
     * Get a subset of MetricName per pair [clientId:topicName]
     *
     * Subset initialised at runtime, each collect() - metrics is scraped;
     */
    public Set<MetricName> getMetricNamesProducerTopicGroup(Set<KeyValue<String, String>> clientIdTopicSet) {
        Set<MetricName> metricNames = new HashSet<>();
        for (KeyValue<String, String> clientIdTopic : clientIdTopicSet) {
            for (MetricNameTemplate metricName : producerTopicMetricsTemplates) {
                metricNames.add(
                        new MetricName(
                                metricName.name(),
                                metricName.group(),
                                metricName.description(),
                                new HashMap<String, String>() {{
                                    put("client-id", clientIdTopic.key);
                                    put("topic", clientIdTopic.value);
                                }}
                        )
                );
            }
        }
        return metricNames;
    }

    /**
     * Get a subset of MetricName per pair [clientId:node]
     */
    public Set<MetricName> getMetricNamesPerBrokerGroup(Set<KeyValue<String, String>> clientIdNodeSet) {
        return perBrokerMetricTemplate.getMetricNamesPerBrokerGroup(clientIdNodeSet);
    }
}
