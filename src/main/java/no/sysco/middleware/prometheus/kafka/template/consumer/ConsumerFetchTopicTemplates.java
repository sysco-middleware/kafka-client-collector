package no.sysco.middleware.prometheus.kafka.template.consumer;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;
import org.apache.kafka.streams.KeyValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ConsumerFetchTopicTemplates {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

    private final MetricNameTemplate bytesConsumedRate;
    private final MetricNameTemplate bytesConsumedTotal;
    private final MetricNameTemplate fetchLatencyAvg;
    private final MetricNameTemplate fetchLatencyMax;
    private final MetricNameTemplate recordsConsumedRate;
    private final MetricNameTemplate recordsConsumedTotal;
    private final MetricNameTemplate recordsPerRequestAvg;

    public ConsumerFetchTopicTemplates() {
        this.metricGroupName = "consumer-fetch-manager-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Arrays.asList("client-id", "topic"));

        this.bytesConsumedRate = createTemplate("bytes-consumed-rate", "The average number of bytes consumed per second for a topic", tags);
        this.bytesConsumedTotal = createTemplate("bytes-consumed-total", "The total number of bytes consumed for a topic", tags);
        this.fetchLatencyAvg = createTemplate("fetch-latency-avg", "The average number of bytes fetched per request for a topic", tags);
        this.fetchLatencyMax = createTemplate("fetch-latency-max", "The maximum number of bytes fetched per request for a topic", tags);
        this.recordsConsumedRate = createTemplate("records-consumed-rate", "The average number of records consumed per second for a topic", tags);
        this.recordsConsumedTotal = createTemplate("records-consumed-total", "The total number of records consumed for a topic", tags);
        this.recordsPerRequestAvg = createTemplate("records-per-request-avg", "The average number of records in each request for a topic", tags);
    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }

    public Set<MetricName> getMetricNames(Set<KeyValue<String, String>> clientIdTopicSet) {
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
