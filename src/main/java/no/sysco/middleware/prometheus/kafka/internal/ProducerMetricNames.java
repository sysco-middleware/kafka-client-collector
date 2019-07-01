package no.sysco.middleware.prometheus.kafka.internal;

import org.apache.kafka.common.MetricName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ProducerMetricNames {
    private final static String PRODUCER_METRICS = "producer-metrics";

    public static Set<MetricName> INITIAL_METRIC_NAMES = new HashSet<>(
            Arrays.asList(
                    new MetricName(
                            "metadata-age",
                            PRODUCER_METRICS,
                            "The age in seconds of the current producer metadata being used.",
                            new HashMap<>()),
                    new MetricName(
                            "record-queue-time-max",
                            PRODUCER_METRICS,
                            "The maximum time in ms record batches spent in the send buffer.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "select-rate",
                            PRODUCER_METRICS,
                            "The number of times the I/O layer checked for new I/O to perform per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "compression-rate-avg",
                            PRODUCER_METRICS,
                            "The average compression rate of record batches.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "produce-throttle-time-avg",
                            PRODUCER_METRICS,
                            "The average time in ms a request was throttled by a broker",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "successful-authentication-rate",
                            PRODUCER_METRICS,
                            "The number of connections with successful authentication per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "request-total",
                            PRODUCER_METRICS,
                            "The total number of requests sent",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-retry-rate",
                            PRODUCER_METRICS,
                            "The average per-second number of retried record sends",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "requests-in-flight",
                            PRODUCER_METRICS,
                            "The total number of requests sent.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "batch-size-max",
                            PRODUCER_METRICS,
                            "The max number of bytes sent per partition per-request.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "outgoing-byte-total",
                            PRODUCER_METRICS,
                            "The total number of outgoing bytes sent to all servers.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-size-avg",
                            PRODUCER_METRICS,
                            "The average record size",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "network-io-total",
                            PRODUCER_METRICS,
                            "The total number of network operations (reads or writes) on all connections",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "io-wait-time-ns-avg",
                            PRODUCER_METRICS,
                            "The average length of time the I/O thread spent waiting for a socket ready for reads or writes in nanoseconds.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "produce-throttle-time-max",
                            PRODUCER_METRICS,
                            "The maximum time in ms a request was throttled by a broker",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "failed-authentication-rate",
                            PRODUCER_METRICS,
                            "The number of connections with failed authentication per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "request-latency-max",
                            PRODUCER_METRICS,
                            "The maximum request latency in ms",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "outgoing-byte-rate",
                            PRODUCER_METRICS,
                            "The number of outgoing bytes sent to all servers per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "bufferpool-wait-time-total",
                            PRODUCER_METRICS,
                            "The total time an appender waits for space allocation.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "connection-creation-rate",
                            PRODUCER_METRICS,
                            "The number of new connections established per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "failed-authentication-total",
                            PRODUCER_METRICS,
                            "The total number of connections with failed authentication",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "response-rate",
                            PRODUCER_METRICS,
                            "The number of responses received per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "batch-split-total",
                            PRODUCER_METRICS,
                            "The total number of batch splits",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "connection-creation-total",
                            PRODUCER_METRICS,
                            "The total number of new connections established",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-retry-total",
                            PRODUCER_METRICS,
                            "The total number of retried record sends",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "io-time-ns-avg",
                            PRODUCER_METRICS,
                            "The average length of time for I/O per select call in nanoseconds.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "connection-count",
                            PRODUCER_METRICS,
                            "The current number of active connections.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "connection-close-total",
                            PRODUCER_METRICS,
                            "The total number of connections closed",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-size-max",
                            PRODUCER_METRICS,
                            "The maximum record size",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-send-total",
                            PRODUCER_METRICS,
                            "The total number of records sent.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "buffer-available-bytes",
                            PRODUCER_METRICS,
                            "The total amount of buffer memory that is not being used (either unallocated or in the free list).",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-error-rate",
                            PRODUCER_METRICS,
                            "The average per-second number of record sends that resulted in errors",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "incoming-byte-total",
                            PRODUCER_METRICS,
                            "The total number of bytes read off all sockets",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "io-ratio",
                            PRODUCER_METRICS,
                            "The fraction of time the I/O thread spent doing I/O",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "request-latency-avg",
                            PRODUCER_METRICS,
                            "The average request latency in ms",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "buffer-exhausted-rate",
                            PRODUCER_METRICS,
                            "The average per-second number of record sends that are dropped due to buffer exhaustion",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "io-waittime-total",
                            PRODUCER_METRICS,
                            "The total time the I/O thread spent waiting",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "incoming-byte-rate",
                            PRODUCER_METRICS,
                            "The number of bytes read off all sockets per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "io-wait-ratio",
                            PRODUCER_METRICS,
                            "The fraction of time the I/O thread spent waiting",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "bufferpool-wait-ratio",
                            PRODUCER_METRICS,
                            "The fraction of time an appender waits for space allocation.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "iotime-total",
                            PRODUCER_METRICS,
                            "The total time the I/O thread spent doing I/O",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "select-total",
                            PRODUCER_METRICS,
                            "The total number of times the I/O layer checked for new I/O to perform",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "waiting-threads",
                            PRODUCER_METRICS,
                            "The number of user threads blocked waiting for buffer memory to enqueue their records",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "buffer-total-bytes",
                            PRODUCER_METRICS,
                            "The maximum amount of buffer memory the client can use (whether or not it is currently used).",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "request-rate",
                            PRODUCER_METRICS,
                            "The number of requests sent per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "buffer-exhausted-total",
                            PRODUCER_METRICS,
                            "The total number of record sends that are dropped due to buffer exhaustion",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-queue-time-avg",
                            PRODUCER_METRICS,
                            "The average time in ms record batches spent in the send buffer.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "batch-split-rate",
                            PRODUCER_METRICS,
                            "The average number of batch splits per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "connection-close-rate",
                            PRODUCER_METRICS,
                            "The number of connections closed per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "successful-authentication-total",
                            PRODUCER_METRICS,
                            "The total number of connections with successful authentication",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "request-size-avg",
                            PRODUCER_METRICS,
                            "The average size of requests sent.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "response-total",
                            PRODUCER_METRICS,
                            "The total number of responses received",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "network-io-rate",
                            PRODUCER_METRICS,
                            "The number of network operations (reads or writes) on all connections per second",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "batch-size-avg",
                            PRODUCER_METRICS,
                            "The average number of bytes sent per partition per-request.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-send-rate",
                            PRODUCER_METRICS,
                            "The average number of records sent per second.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "records-per-request-avg",
                            PRODUCER_METRICS,
                            "The average number of records per request.",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "record-error-total",
                            PRODUCER_METRICS,
                            "The total number of record sends that resulted in errors",
                            new HashMap<>()
                    ))
    );

    public static Set<MetricName> getMetricNames(Set<String> clientIds) {
        Set<MetricName> metricNames = new HashSet<>();
        for (String clientId : clientIds) {
            for (MetricName metricName : INITIAL_METRIC_NAMES) {
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

}
