package no.sysco.middleware.prometheus.kafka.internal;

import org.apache.kafka.common.MetricNameTemplate;

import java.util.*;

// example:  kafka.[producer|consumer|connect]:type=[consumer|producer|connect]-node-metrics,client-id=([-.\w]+),node-id=([0-9]+)
public class PerBrokerMetricTemplate {
    /**
     * Common metrics per-broker https://kafka.apache.org/documentation/#common_node_monitoring
     */
    private final String metricGroupName;
    private final Set<MetricNameTemplate> perBrokerMetricTemplates;

    private final MetricNameTemplate outgoingByteRate;
    private final MetricNameTemplate outgoingByteTotal;
    private final MetricNameTemplate requestRate;
    private final MetricNameTemplate requestTotal;
    private final MetricNameTemplate requestSizeAvg;
    private final MetricNameTemplate requestSizeMax;
    private final MetricNameTemplate incomingByteRate;
    private final MetricNameTemplate incomingByteTotal;
    private final MetricNameTemplate requestLatencyAvg;
    private final MetricNameTemplate requestLatencyMax;
    private final MetricNameTemplate responseRate;
    private final MetricNameTemplate responseTotal;

    public PerBrokerMetricTemplate(KafkaClient kafkaCLient) {
        this.metricGroupName = kafkaCLient + "-node-metrics";
        this.perBrokerMetricTemplates = new HashSet<>();
        HashSet<String> tags = new HashSet<>(Arrays.asList("client-id", "node-id"));

        this.outgoingByteRate = createTemplate("outgoing-byte-rate", "The number of outgoing bytes per second.", tags);
        this.outgoingByteTotal = createTemplate("outgoing-byte-total", "The total number of outgoing bytes sent for a node.", tags);
        this.requestRate = createTemplate("request-rate", "The average number of requests sent per second for a node.", tags);
        this.requestTotal = createTemplate("request-total", "The total number of requests sent for a node.", tags);
        this.requestSizeAvg = createTemplate("request-size-avg", "The average size of all requests in the window for a node.", tags);
        this.requestSizeMax = createTemplate("request-size-max", "The maximum size of any request sent in the window for a node.", tags);
        this.incomingByteRate = createTemplate("incoming-byte-rate", "The average number of bytes received per second for a node.", tags);
        this.incomingByteTotal = createTemplate("incoming-byte-total", "The total number of bytes received for a node.", tags);
        this.requestLatencyAvg = createTemplate("request-latency-avg", "The average request latency in ms for a node.", tags);
        this.requestLatencyMax = createTemplate("request-latency-max", "The maximum request latency in ms for a node.", tags);
        this.responseRate = createTemplate("response-rate", "Responses received per second for a node.", tags);
        this.responseTotal = createTemplate("response-total", "Total responses received for a node.", tags);
    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        perBrokerMetricTemplates.add(metricNameTemplate);
        return metricNameTemplate;
    }


}
