package no.sysco.middleware.prometheus.kafka.template.producer;

import org.apache.kafka.common.MetricNameTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// https://kafka.apache.org/documentation/#producer_sender_monitoring
public class ProducerInstanceTemplates {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

    // exist when producer communicate with cluster
    private final MetricNameTemplate waitingThreads;
    private final MetricNameTemplate bufferTotalBytes;
    private final MetricNameTemplate bufferAvailableBytes;
    private final MetricNameTemplate bufferpoolWaitTime;

    // exist when producer is initialised
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

    public ProducerInstanceTemplates() {
        this.metricGroupName = "producer-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Arrays.asList("client-id"));

        this.waitingThreads = createTemplate("waiting-threads", "The number of user threads blocked waiting for buffer memory to enqueue their records.", tags);
        this.bufferTotalBytes = createTemplate("buffer-total-bytes", "The maximum amount of buffer memory the client can use (whether or not it is currently used).", tags);
        this.bufferAvailableBytes = createTemplate("buffer-available-bytes", "The total amount of buffer memory that is not being used (either unallocated or in the free list).", tags);
        this.bufferpoolWaitTime = createTemplate("bufferpool-wait-time", "The fraction of time an appender waits for space allocation.", tags);

        this.batchSizeAvg = createTemplate("batch-size-avg", "The average number of bytes sent per partition per-request.", tags);
        this.batchSizeMax = createTemplate("batch-size-max", "The max number of bytes sent per partition per-request.", tags);
        this.compressionRateAvg = createTemplate("compression-rate-avg", "The average compression rate of record batches.", tags);
        this.recordQueueTimeAvg = createTemplate("record-queue-time-avg", "The average time in ms record batches spent in the send buffer.", tags);
        this.recordQueueTimeMax = createTemplate("record-queue-time-max", "The maximum time in ms record batches spent in the send buffer.", tags);
        this.requestLatencyAvg = createTemplate("request-latency-avg", "The average request latency in ms", tags);
        this.requestLatencyMax = createTemplate("request-latency-max", "The maximum request latency in ms", tags);
        this.recordSendRate = createTemplate("record-send-rate", "The average number of records sent per second.", tags);
        this.recordSendTotal = createTemplate("record-send-total", "The total number of records sent.", tags);
        this.recordsPerRequestAvg = createTemplate("records-per-request-avg", "The average number of records per request.", tags);
        this.recordRetryRate = createTemplate("record-retry-rate", "The average per-second number of retried record sends", tags);
        this.recordRetryTotal = createTemplate("record-retry-total", "The total number of retried record sends", tags);
        this.recordErrorRate = createTemplate("record-error-rate", "The average per-second number of record sends that resulted in errors", tags);
        this.recordErrorTotal = createTemplate("record-error-total", "The total number of record sends that resulted in errors", tags);
        this.recordSizeMax = createTemplate("record-size-max", "The maximum record size", tags);
        this.recordSizeAvg = createTemplate("record-size-avg", "The average record size", tags);
        this.requestsInFlight = createTemplate("requests-in-flight", "The current number of in-flight requests awaiting a response.", tags);
        this.metadataAge = createTemplate("metadata-age", "The age in seconds of the current producer metadata being used.", tags);
        this.batchSplitRate = createTemplate("batch-split-rate", "The average number of batch splits per second", tags);
        this.batchSplitTotal = createTemplate("batch-split-total", "The total number of batch splits", tags);
        this.produceThrottleTimeAvg = createTemplate("produce-throttle-time-avg", "The average time in ms a request was throttled by a broker", tags);
        this.produceThrottleTimeMax = createTemplate("produce-throttle-time-max", "The maximum time in ms a request was throttled by a broker", tags);

    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }
}
