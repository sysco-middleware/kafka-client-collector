package no.sysco.middleware.prometheus.kafka.template.consumer;

import org.apache.kafka.common.MetricNameTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConsumerGroupTemplates {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

    private final MetricNameTemplate commitLatencyAvg;
    private final MetricNameTemplate commitLatencyMax;
    private final MetricNameTemplate commitRate;
    private final MetricNameTemplate commitTotal;
    private final MetricNameTemplate assignedPartitions;
    private final MetricNameTemplate heartbeatResponseTimeMax;
    private final MetricNameTemplate heartbeatRate;
    private final MetricNameTemplate heartbeatTotal;
    private final MetricNameTemplate joinTimeAvg;
    private final MetricNameTemplate joinTimeMax;
    private final MetricNameTemplate joinRate;
    private final MetricNameTemplate joinTotal;
    private final MetricNameTemplate syncTimeAvg;
    private final MetricNameTemplate syncTimeMax;
    private final MetricNameTemplate syncRate;
    private final MetricNameTemplate syncTotal;
    private final MetricNameTemplate lastHeartbeatSecondsAgo;

    public ConsumerGroupTemplates() {
        this.metricGroupName = "consumer-coordinator-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Arrays.asList("client-id"));

        this.commitLatencyAvg = createTemplate("commit-latency-avg", "The average time taken for a commit request", tags);
        this.commitLatencyMax = createTemplate("commit-latency-max", "The max time taken for a commit request", tags);
        this.commitRate = createTemplate("commit-rate", "The number of commit calls per second", tags);
        this.commitTotal = createTemplate("commit-total", "The total number of commit calls", tags);
        this.assignedPartitions = createTemplate("assigned-partitions", "The number of partitions currently assigned to this consumer", tags);
        this.heartbeatResponseTimeMax = createTemplate("heartbeat-response-time-max", "The max time taken to receive a response to a heartbeat request", tags);
        this.heartbeatRate = createTemplate("heartbeat-rate", "The average number of heartbeats per second", tags);
        this.heartbeatTotal = createTemplate("heartbeat-total", "The total number of heartbeats", tags);
        this.joinTimeAvg = createTemplate("join-time-avg", "The average time taken for a group rejoin", tags);
        this.joinTimeMax = createTemplate("join-time-max", "The max time taken for a group rejoin", tags);
        this.joinRate = createTemplate("join-rate", "The number of group joins per second", tags);
        this.joinTotal = createTemplate("join-total", "The total number of group joins", tags);
        this.syncTimeAvg = createTemplate("sync-time-avg", "The average time taken for a group sync", tags);
        this.syncTimeMax = createTemplate("sync-time-max", "The max time taken for a group sync", tags);
        this.syncRate = createTemplate("sync-rate", "The number of group syncs per second", tags);
        this.syncTotal = createTemplate("sync-total", "The total number of group syncs", tags);
        this.lastHeartbeatSecondsAgo = createTemplate("last-heartbeat-seconds-ago", "The number of seconds since the last controller heartbeat", tags);
    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }
}
