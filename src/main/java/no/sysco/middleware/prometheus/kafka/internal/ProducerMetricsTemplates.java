package no.sysco.middleware.prometheus.kafka.internal;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;
import org.apache.kafka.streams.KeyValue;

import java.util.*;
import java.util.stream.Collectors;

public class ProducerMetricsTemplates {

    /**
     * Value of KafkaProducer.JMX_PREFIX
     *
     * Value is mapped to mBean domain
     * */
    public final static String PRODUCER_DOMAIN = "kafka.producer"; //

    /**
     * Value of KafkaProducer.PRODUCER_METRIC_GROUP_NAME
     *
     * This type of metrics initialised at startup of application,
     * when producers have already been initialized
     * */
    public final static String PRODUCER_METRIC_GROUP_NAME = "producer-metrics";

    /**
     * Value of SenderMetricsRegistry.TOPIC_METRIC_GROUP_NAME
     *
     * This type of metrics initialised at startup of application,
     * when producers have already been initialized
     * */
    public final static String PRODUCER_TOPIC_METRIC_GROUP_NAME = "producer-topic-metrics";

    private final List<MetricNameTemplate> allTemplates;

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
        this.clientTags = new HashSet<>();
        this.allTemplates = new ArrayList<>();

        /***** Client level *****/
        /****** at start time ******/
        clientTags.add("client-id");
        this.batchSizeAvg = createTemplate("batch-size-avg",
                "The average number of bytes sent per partition per-request.");
        this.batchSizeMax = createTemplate("batch-size-max",
                "The max number of bytes sent per partition per-request.");
        this.compressionRateAvg = createTemplate("compression-rate-avg",
                "The average compression rate of record batches.");
        this.recordQueueTimeAvg = createTemplate("record-queue-time-avg",
                "The average time in ms record batches spent in the send buffer.");
        this.recordQueueTimeMax = createTemplate("record-queue-time-max",
                "The maximum time in ms record batches spent in the send buffer.");
        this.requestLatencyAvg = createTemplate("request-latency-avg",
                "The average request latency in ms");
        this.requestLatencyMax = createTemplate("request-latency-max",
                "The maximum request latency in ms");
        this.recordSendRate = createTemplate("record-send-rate",
                "The average number of records sent per second.");
        this.recordSendTotal = createTemplate("record-send-total",
                "The total number of records sent.");
        this.recordsPerRequestAvg = createTemplate("records-per-request-avg",
                "The average number of records per request.");
        this.recordRetryRate = createTemplate("record-retry-rate",
                "The average per-second number of retried record sends");
        this.recordRetryTotal = createTemplate("record-retry-total",
                "The total number of retried record sends");
        this.recordErrorRate = createTemplate("record-error-rate",
                "The average per-second number of record sends that resulted in errors");
        this.recordErrorTotal = createTemplate("record-error-total",
                "The total number of record sends that resulted in errors");
        this.recordSizeMax = createTemplate("record-size-max",
                "The maximum record size");
        this.recordSizeAvg = createTemplate("record-size-avg",
                "The average record size");
        this.requestsInFlight = createTemplate("requests-in-flight",
                "The current number of in-flight requests awaiting a response.");
        this.metadataAge = createTemplate("metadata-age",
                "The age in seconds of the current producer metadata being used.");
        this.batchSplitRate = createTemplate("batch-split-rate",
                "The average number of batch splits per second");
        this.batchSplitTotal = createTemplate("batch-split-total",
                "The total number of batch splits");
        this.produceThrottleTimeAvg = createTemplate("produce-throttle-time-avg",
                "The average time in ms a request was throttled by a broker");
        this.produceThrottleTimeMax = createTemplate("produce-throttle-time-max",
                "The maximum time in ms a request was throttled by a broker");

        /***** Topic level *****/
        /****** at run time when producer start send data ******/

        this.topicTags = new LinkedHashSet<>(clientTags);
        this.topicTags.add("topic");

        // We can't create the MetricName up front for these, because we don't know the topic name yet.
        this.topicRecordSendRate = createTopicTemplate("record-send-rate",
                "The average number of records sent per second for a topic.");
        this.topicRecordSendTotal = createTopicTemplate("record-send-total",
                "The total number of records sent for a topic.");
        this.topicByteRate = createTopicTemplate("byte-rate",
                "The average number of bytes sent per second for a topic.");
        this.topicByteTotal = createTopicTemplate("byte-total",
                "The total number of bytes sent for a topic.");
        this.topicCompressionRate = createTopicTemplate("compression-rate",
                "The average compression rate of record batches for a topic.");
        this.topicRecordRetryRate = createTopicTemplate("record-retry-rate",
                "The average per-second number of retried record sends for a topic");
        this.topicRecordRetryTotal = createTopicTemplate("record-retry-total",
                "The total number of retried record sends for a topic");
        this.topicRecordErrorRate = createTopicTemplate("record-error-rate",
                "The average per-second number of record sends that resulted in errors for a topic");
        this.topicRecordErrorTotal = createTopicTemplate("record-error-total",
                "The total number of record sends that resulted in errors for a topic");
    }

    private MetricNameTemplate createTemplate(String name, String description) {
        return createTemplate(name, PRODUCER_METRIC_GROUP_NAME, description, clientTags);
    }

    private MetricNameTemplate createTopicTemplate(String name, String description) {
        return createTemplate(name, PRODUCER_TOPIC_METRIC_GROUP_NAME, description, topicTags);
    }

    private MetricNameTemplate createTemplate(String name, String group, String description, Set<String> tags) {
        MetricNameTemplate template = new MetricNameTemplate(name, group, description, tags);
        this.allTemplates.add(template);
        return template;
    }

    public List<MetricNameTemplate> getAllTemplates() {
        return allTemplates;
    }

    // at startup
    public Set<MetricNameTemplate> getClientLevel() {
        return allTemplates.stream().filter(template -> PRODUCER_METRIC_GROUP_NAME.equals(template.group())).collect(Collectors.toSet());
    }

    // dynamic at runtime
    public Set<MetricNameTemplate> getTopicLevel() {
        return allTemplates.stream().filter(template -> PRODUCER_TOPIC_METRIC_GROUP_NAME.equals(template.group())).collect(Collectors.toSet());
    }

    // called once at start
    public Set<MetricName> getMetricNamesPerClientId(Set<String> clientIds) {
        Set<MetricName> metricNames = new HashSet<>();
        for (String clientId : clientIds) {
            for (MetricNameTemplate metricName : getClientLevel()) {
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

    // called on each collect
    //todo: refactor
    // create a subset of MetricName per each pair [clientId:topicName]
    public Set<MetricName> getMetricNamesClientIdTopic(List<KeyValue<String, String>> clientIdTopicList) {
        Set<MetricName> metricNames = new HashSet<>();
        for (KeyValue<String, String> clientIdTopic : clientIdTopicList) {
            for (MetricNameTemplate metricName : getTopicLevel()) {
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
}
