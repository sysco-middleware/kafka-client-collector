package no.sysco.middleware.prometheus.kafka.template.stream;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;
import org.apache.kafka.streams.KeyValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// All the following metrics have a recording level of debug
public class StreamTaskMetricsTemplates {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

    // exist when stream communicate with cluster
    private final MetricNameTemplate commitLatencyAvg;
    private final MetricNameTemplate commitLatencyMax;
    private final MetricNameTemplate commitRate;
    private final MetricNameTemplate commitTotal;
    private final MetricNameTemplate recordLatenessAvg;
    private final MetricNameTemplate recordLatenessMax;

    public StreamTaskMetricsTemplates() {
        this.metricGroupName = "stream-task-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Arrays.asList("client-id", "task-id"));

        this.commitLatencyAvg = createTemplate("commit-latency-avg", "The average commit time in ns for this task.", tags);
        this.commitLatencyMax = createTemplate("commit-latency-max", "The maximum commit time in ns for this task.", tags);
        this.commitRate = createTemplate("commit-rate", "The average number of commit calls per second.", tags);
        this.commitTotal = createTemplate("commit-total", "The total number of commit calls.", tags);
        this.recordLatenessAvg = createTemplate("record-lateness-avg", "The average observed lateness of records.", tags);
        this.recordLatenessMax = createTemplate("record-lateness-max", "The max observed lateness of records.", tags);
    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }

    /**
     * Get a subset of MetricName per pair [clientId:taskId]
     */
    public Set<MetricName> getMetricNames(Set<KeyValue<String, String>> clientIdTaskSet) {
        Set<MetricName> metricNames = new HashSet<>();
        for (KeyValue<String, String> clientIdTask : clientIdTaskSet) {
            for (MetricNameTemplate metricName : templates) {
                metricNames.add(
                        new MetricName(
                                metricName.name(),
                                metricName.group(),
                                metricName.description(),
                                new HashMap<String, String>() {{
                                    put("client-id", clientIdTask.key);
                                    put("task-id", clientIdTask.value);
                                }}
                        )
                );
            }
        }
        return metricNames;
    }
}
