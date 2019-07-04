package no.sysco.middleware.prometheus.kafka.template;

import no.sysco.middleware.prometheus.kafka.template.common.KafkaClient;
import no.sysco.middleware.prometheus.kafka.template.common.PerBrokerTemplates;
import org.apache.kafka.common.MetricNameTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ConsumerMetricTemplates has ? group of metrics
 *
 * 1. `consumer-metrics`
 * 2. `consumer-coordinator-metrics`
 * 3. `consumer-fetch-manager-metrics`
 */
public class ConsumerMetricTemplates {
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

    /** templates */
    // common templates
    private final PerBrokerTemplates perBrokerTemplates;
    // consumer only templates
    private final Set<MetricNameTemplate> consumerMetricsTemplates;

    // consumer-coordinator-metrics
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

    // consumer-fetch-manager-metrics
//    private final MetricNameTemplate commitLatencyAvg;


    private final Set<String> clientTags; // client-id

    public ConsumerMetricTemplates() {
        this.perBrokerTemplates = new PerBrokerTemplates(KafkaClient.CONSUMER);
        this.consumerMetricsTemplates = new HashSet<>();
        this.clientTags = new HashSet<>(Collections.singletonList("client-id"));

        this.commitLatencyAvg = createTemplate("commit-latency-avg", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The average time taken for a commit request", clientTags);
        this.commitLatencyMax = createTemplate("commit-latency-max", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The max time taken for a commit request", clientTags);
        this.commitRate = createTemplate("commit-rate", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The number of commit calls per second", clientTags);
        this.commitTotal = createTemplate("commit-total", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The total number of commit calls", clientTags);
        this.assignedPartitions = createTemplate("assigned-partitions", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The number of partitions currently assigned to this consumer", clientTags);
        this.heartbeatResponseTimeMax = createTemplate("heartbeat-response-time-max", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The max time taken to receive a response to a heartbeat request", clientTags);
        this.heartbeatRate = createTemplate("heartbeat-rate", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The average number of heartbeats per second", clientTags);
        this.heartbeatTotal = createTemplate("heartbeat-total", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The total number of heartbeats", clientTags);
        this.joinTimeAvg = createTemplate("join-time-avg", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The average time taken for a group rejoin", clientTags);
        this.joinTimeMax = createTemplate("join-time-max", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The max time taken for a group rejoin", clientTags);
        this.joinRate = createTemplate("join-rate", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The number of group joins per second", clientTags);
        this.joinTotal = createTemplate("join-total", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The total number of group joins", clientTags);
        this.syncTimeAvg = createTemplate("sync-time-avg", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The average time taken for a group sync", clientTags);
        this.syncTimeMax = createTemplate("sync-time-max", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The max time taken for a group sync", clientTags);
        this.syncRate = createTemplate("sync-rate", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The number of group syncs per second", clientTags);
        this.syncTotal = createTemplate("sync-total", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The total number of group syncs", clientTags);
        this.lastHeartbeatSecondsAgo = createTemplate("last-heartbeat-seconds-ago", CONSUMER_COORDINATOR_METRIC_GROUP_NAME, "The number of seconds since the last controller heartbeat", clientTags);

    }

    private MetricNameTemplate createTemplate(String name, String metricGroupName, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        if (CONSUMER_METRIC_GROUP_NAME.equals(metricGroupName)) {
            consumerMetricsTemplates.add(metricNameTemplate);
        } else {
            throw new IllegalArgumentException("Unknown metric group " + metricGroupName);
        }
        return metricNameTemplate;
    }
}
