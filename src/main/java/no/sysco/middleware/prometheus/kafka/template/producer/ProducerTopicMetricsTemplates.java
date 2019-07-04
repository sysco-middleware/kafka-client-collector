package no.sysco.middleware.prometheus.kafka.template.producer;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;
import org.apache.kafka.streams.KeyValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ProducerTopicMetricsTemplates {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;


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


    public ProducerTopicMetricsTemplates() {
        this.metricGroupName = "producer-topic-metrics";
        this.templates = new HashSet<>();
        Set<String> topicTags = new HashSet<>(Arrays.asList("client-id", "topic"));

        /***** Topic level *****/
        /****** at run time when producer start send data ******/

        // We can't create the MetricName up front for these, because we don't know the topic name yet.
        this.topicRecordSendRate = createTemplate("record-send-rate", "The average number of records sent per second for a topic.", topicTags);
        this.topicRecordSendTotal = createTemplate("record-send-total", "The total number of records sent for a topic.", topicTags);
        this.topicByteRate = createTemplate("byte-rate", "The average number of bytes sent per second for a topic.", topicTags);
        this.topicByteTotal = createTemplate("byte-total", "The total number of bytes sent for a topic.", topicTags);
        this.topicCompressionRate = createTemplate("compression-rate", "The average compression rate of record batches for a topic.", topicTags);
        this.topicRecordRetryRate = createTemplate("record-retry-rate", "The average per-second number of retried record sends for a topic", topicTags);
        this.topicRecordRetryTotal = createTemplate("record-retry-total", "The total number of retried record sends for a topic", topicTags);
        this.topicRecordErrorRate = createTemplate("record-error-rate", "The average per-second number of record sends that resulted in errors for a topic", topicTags);
        this.topicRecordErrorTotal = createTemplate("record-error-total", "The total number of record sends that resulted in errors for a topic", topicTags);
    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }
    /**
     * Get a subset of MetricName per pair [clientId:topicName]
     *
     * Subset initialised at runtime, each collect() - metrics is scraped;
     */
    public Set<MetricName> getMetricNamesProducerTopicGroup(Set<KeyValue<String, String>> clientIdTopicSet) {
        Set<MetricName> metricNames = new HashSet<>();
        for (KeyValue<String, String> clientIdTopic : clientIdTopicSet) {
            for (MetricNameTemplate metricName : templates) {
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
