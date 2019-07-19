package no.sysco.middleware.prometheus.kafka.template;

import no.sysco.middleware.prometheus.kafka.template.stream.StreamThreadMetricsTemplates;
import org.apache.kafka.common.MetricName;

import java.util.Set;

/**
 * StreamMetricTemplates
 * https://kafka.apache.org/documentation/#kafka_streams_monitoring
 */
public class StreamMetricTemplates extends MetricTemplates {
    /**
     * MBean domain.
     * NB!
     * Kafka streams, under the hood, has producer and consumer.
     * When use `kafka stream api` there are 3 domains will be registered:
     * 1. kafka.producer - producer related metrics
     * 2. kafka.consumer - consumer related metrics
     * 3. kafka.stream - stream related
     * <p>
     * Metrics for each domain is handled by `no.sysco.middleware.prometheus.kafka.ClientsJmxCollector`
     */
    public final static String STREAM_DOMAIN = "kafka.streams";

    /**
     * Metric Groups
     */
    public final static String STREAM_METRIC_GROUP_NAME = "stream-metrics";

    /** common templates
     *  producer and consumers for kafka stream
     * */

    /**
     * stream only templates
     */
    public final StreamThreadMetricsTemplates streamThreadMetricsTemplates; // `stream-metrics`

    public StreamMetricTemplates() {
        this.streamThreadMetricsTemplates = new StreamThreadMetricsTemplates();
    }

    // single client-id
    public Set<MetricName> getMetricNamesStreamThread(Set<String> clientIdSet) {
        return getMetricNamesPerClientId(clientIdSet, streamThreadMetricsTemplates.templates);
    }

}
