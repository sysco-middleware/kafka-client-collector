package no.sysco.middleware.prometheus.kafka.template.consumer;

import org.apache.kafka.common.MetricNameTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConsumerFetchTemplates {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

    private final MetricNameTemplate bytesConsumedRate;
    private final MetricNameTemplate bytesConsumedTotal;
    private final MetricNameTemplate fetchLatencyAvg;
    private final MetricNameTemplate fetchLatencyMax;
    private final MetricNameTemplate fetchRate;
    private final MetricNameTemplate fetchSizeAvg;
    private final MetricNameTemplate fetchSizeMax;
    private final MetricNameTemplate fetchThrottleTimeAvg;
    private final MetricNameTemplate fetchThrottleTimeMax;
    private final MetricNameTemplate fetchTotal;
    private final MetricNameTemplate recordsConsumedRate;
    private final MetricNameTemplate recordsConsumedTotal;
    private final MetricNameTemplate recordsLagMax;
    private final MetricNameTemplate recordsLeadMin;
    private final MetricNameTemplate recordsPerRequestAvg;

    public ConsumerFetchTemplates() {
        this.metricGroupName = "consumer-fetch-manager-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Arrays.asList("client-id"));

        this.bytesConsumedRate = createTemplate("bytes-consumed-rate", "The average number of bytes consumed per second", tags);
        this.bytesConsumedTotal = createTemplate("bytes-consumed-total", "The total number of bytes consumed", tags);
        this.fetchLatencyAvg = createTemplate("fetch-latency-avg", "The average time taken for a fetch request.", tags);
        this.fetchLatencyMax = createTemplate("fetch-latency-max", "The max time taken for any fetch request.", tags);
        this.fetchRate = createTemplate("fetch-rate", "The number of fetch requests per second.", tags);
        this.fetchSizeAvg = createTemplate("fetch-size-avg", "The average number of bytes fetched per request", tags);
        this.fetchSizeMax = createTemplate("fetch-size-max", "The maximum number of bytes fetched per request", tags);
        this.fetchThrottleTimeAvg = createTemplate("fetch-throttle-time-avg", "The average throttle time in ms", tags);
        this.fetchThrottleTimeMax = createTemplate("fetch-throttle-time-max", "The maximum throttle time in ms", tags);
        this.fetchTotal = createTemplate("fetch-total", "The total number of fetch requests.", tags);
        this.recordsConsumedRate = createTemplate("records-consumed-rate", "The average number of records consumed per second", tags);
        this.recordsConsumedTotal = createTemplate("records-consumed-total", "The total number of records consumed", tags);
        this.recordsLagMax = createTemplate("records-lag-max", "The maximum lag in terms of number of records for any partition in this window", tags);
        this.recordsLeadMin = createTemplate("records-lead-min", "The minimum lead in terms of number of records for any partition in this window", tags);
        this.recordsPerRequestAvg = createTemplate("records-per-request-avg", "The average number of records in each request", tags);
    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }

}
