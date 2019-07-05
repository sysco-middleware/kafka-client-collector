package no.sysco.middleware.prometheus.kafka.template.common;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;
import org.apache.kafka.streams.KeyValue;

import java.util.*;

/**
 * PerBrokerTemplates class has templates for common metrics for kafka clients per broker.
 * https://kafka.apache.org/documentation/#common_node_monitoring
 * kafka.[producer|consumer|connect]:type=[consumer|producer|connect]-node-metrics,client-id=([-.\w]+),node-id=([0-9]+)
 */
public class PerBrokerTemplates {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

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

    public PerBrokerTemplates(KafkaClient kafkaCLient) {
        this.metricGroupName = kafkaCLient + "-node-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Arrays.asList("client-id", "node-id"));

        /****** at runtime when client start communicate with node(s) ******/
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
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }

    /**
     * Get a subset of MetricName per pair [clientId:node]
     */
    public Set<MetricName> getMetricNamesPerBrokerGroup(Set<KeyValue<String, String>> clientIdNodeSet) {
        Set<MetricName> metricNames = new HashSet<>();
        for (KeyValue<String, String> clientIdTopic : clientIdNodeSet) {
            for (MetricNameTemplate metricName : templates) {
                metricNames.add(
                        new MetricName(
                                metricName.name(),
                                metricName.group(),
                                metricName.description(),
                                new HashMap<String, String>() {{
                                    put("client-id", clientIdTopic.key);
                                    put("node-id", clientIdTopic.value);
                                }}
                        )
                );
            }
        }
        return metricNames;
    }
}
