package no.sysco.middleware.prometheus.kafka.template.stream;

import org.apache.kafka.common.MetricNameTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// https://kafka.apache.org/documentation/#kafka_streams_thread_monitoring
public class StreamThreadMetricsTemplates {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

    // exist when stream communicate with cluster
    private final MetricNameTemplate commitLatencyAvg;
    private final MetricNameTemplate commitLatencyMax;
    private final MetricNameTemplate pollLatencyAvg;
    private final MetricNameTemplate pollLatencyMax;
    private final MetricNameTemplate processLatencyAvg;
    private final MetricNameTemplate processLatencyMax;
    private final MetricNameTemplate punctuateLatencyAvg;
    private final MetricNameTemplate punctuateLatencyMax;
    private final MetricNameTemplate commitRate;
    private final MetricNameTemplate commitTotal;
    private final MetricNameTemplate pollRate;
    private final MetricNameTemplate pollTotal;
    private final MetricNameTemplate processTotal;
    private final MetricNameTemplate punctuateRate;
    private final MetricNameTemplate punctuateTotal;
    private final MetricNameTemplate taskCreatedRate;
    private final MetricNameTemplate taskCreatedTotal;
    private final MetricNameTemplate taskClosedRate;
    private final MetricNameTemplate taskClosedTotal;
    private final MetricNameTemplate skippedRecordsRate;
    private final MetricNameTemplate skippedRecordsTotal;

    public StreamThreadMetricsTemplates() {
        this.metricGroupName = "stream-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Arrays.asList("client-id"));

        this.commitLatencyAvg = createTemplate("commit-latency-avg", "The average execution time in ms for committing, across all running tasks of this thread.", tags);
        this.commitLatencyMax = createTemplate("commit-latency-max", "The maximum execution time in ms for committing across all running tasks of this thread.", tags);
        this.pollLatencyAvg = createTemplate("poll-latency-avg", "The average execution time in ms for polling, across all running tasks of this thread.", tags);
        this.pollLatencyMax = createTemplate("poll-latency-max", "The maximum execution time in ms for polling across all running tasks of this thread.", tags);
        this.processLatencyAvg = createTemplate("process-latency-avg", "The average execution time in ms for processing, across all running tasks of this thread.", tags);
        this.processLatencyMax = createTemplate("process-latency-max", "The maximum execution time in ms for processing across all running tasks of this thread.", tags);
        this.punctuateLatencyAvg = createTemplate("punctuate-latency-avg", "The average execution time in ms for punctuating, across all running tasks of this thread.", tags);
        this.punctuateLatencyMax = createTemplate("punctuate-latency-max", "The maximum execution time in ms for punctuating across all running tasks of this thread.", tags);
        this.commitRate = createTemplate("commit-rate", "The average number of commits per second.", tags);
        this.commitTotal = createTemplate("commit-total", "The total number of commit calls across all tasks.", tags);
        this.pollRate = createTemplate("poll-rate", "The average number of polls per second.", tags);
        this.pollTotal = createTemplate("poll-total", "The total number of poll calls across all tasks.", tags);
        this.processTotal = createTemplate("process-total", "The total number of process calls across all tasks.", tags);
        this.punctuateRate = createTemplate("punctuate-rate", "The average number of punctuates per second.", tags);
        this.punctuateTotal = createTemplate("punctuate-total", "The total number of punctuate calls across all tasks.", tags);
        this.taskCreatedRate = createTemplate("task-created-rate", "The average number of newly created tasks per second.", tags);
        this.taskCreatedTotal = createTemplate("task-created-total", "The total number of tasks created.", tags);
        this.taskClosedRate = createTemplate("task-closed-rate", "The average number of tasks closed per second.", tags);
        this.taskClosedTotal = createTemplate("task-closed-total", "The total number of tasks closed.", tags);
        this.skippedRecordsRate = createTemplate("skipped-records-rate", "The average number of skipped records per second.", tags);
        this.skippedRecordsTotal = createTemplate("skipped-records-total", "The total number of skipped records.", tags);


    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }
}
