package no.sysco.middleware.prometheus.kafka.template.stream;

import no.sysco.middleware.prometheus.kafka.internal.Tuple3;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// All the following metrics have a recording level of debug
public class StreamProcessorNodeMetricsTemplate {
    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

    private final MetricNameTemplate processLatencyAvg;
    private final MetricNameTemplate processLatencyMax;
    private final MetricNameTemplate punctuateLatencyAvg;
    private final MetricNameTemplate punctuateLatencyMax;
    private final MetricNameTemplate createLatencyAvg;
    private final MetricNameTemplate createLatencyMax;
    private final MetricNameTemplate destroyLatencyAvg;
    private final MetricNameTemplate destroyLatencyMax;
    private final MetricNameTemplate processRate;
    private final MetricNameTemplate processTotal;
    private final MetricNameTemplate punctuateRate;
    private final MetricNameTemplate punctuateTotal;
    private final MetricNameTemplate createRate;
    private final MetricNameTemplate createTotal;
    private final MetricNameTemplate destroyRate;
    private final MetricNameTemplate destroyTotal;
    private final MetricNameTemplate forwardRate;
    private final MetricNameTemplate forwardTotal;
    private final MetricNameTemplate suppressionEmitRate;
    private final MetricNameTemplate suppressionEmitTotal;

    public StreamProcessorNodeMetricsTemplate() {
        this.metricGroupName = "stream-processor-node-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Arrays.asList("client-id", "task-id", "processor-node-id"));

        this.processLatencyAvg = createTemplate("process-latency-avg", "The average process execution time in ns.", tags);
        this.processLatencyMax = createTemplate("process-latency-max", "The maximum process execution time in ns.", tags);
        this.punctuateLatencyAvg = createTemplate("punctuate-latency-avg", "The average punctuate execution time in ns.", tags);
        this.punctuateLatencyMax = createTemplate("punctuate-latency-max", "The maximum punctuate execution time in ns.", tags);
        this.createLatencyAvg = createTemplate("create-latency-avg", "The average create execution time in ns.", tags);
        this.createLatencyMax = createTemplate("create-latency-max", "The maximum create execution time in ns.", tags);
        this.destroyLatencyAvg = createTemplate("destroy-latency-avg", "The average destroy execution time in ns.", tags);
        this.destroyLatencyMax = createTemplate("destroy-latency-max", "The maximum destroy execution time in ns.", tags);
        this.processRate = createTemplate("process-rate", "The average number of process operations per second.", tags);
        this.processTotal = createTemplate("process-total", "The total number of process operations called.", tags);
        this.punctuateRate = createTemplate("punctuate-rate", "The average number of punctuate operations per second.", tags);
        this.punctuateTotal = createTemplate("punctuate-total", "The total number of punctuate operations called.", tags);
        this.createRate = createTemplate("create-rate", "The average number of create operations per second.", tags);
        this.createTotal = createTemplate("create-total", "The total number of create operations called.", tags);
        this.destroyRate = createTemplate("destroy-rate", "The average number of destroy operations per second.", tags);
        this.destroyTotal = createTemplate("destroy-total", "The total number of destroy operations called.", tags);
        this.forwardRate = createTemplate("forward-rate", "The average rate of records being forwarded downstream, from source nodes only, per second.", tags);
        this.forwardTotal = createTemplate("forward-total", "The total number of of records being forwarded downstream, from source nodes only.", tags);
        this.suppressionEmitRate = createTemplate("suppression-emit-rate", "The rate at which records that have been emitted downstream from suppression operation nodes. Compare with the process-rate metric to determine how many updates are being suppressed.", tags);
        this.suppressionEmitTotal = createTemplate("suppression-emit-total", "The total number of records that have been emitted downstream from suppression operation nodes. Compare with the process-total metric to determine how many updates are being suppressed.", tags);

    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }

    /**
     * Get a subset of MetricName per pair [clientId:taskId]
     */
    public Set<MetricName> getMetricNames(Set<Tuple3<String, String, String>> clientIdTaskNodeIdSet) {
        Set<MetricName> metricNames = new HashSet<>();
        for (Tuple3<String, String, String> clientIdTaskNodeId : clientIdTaskNodeIdSet) {
            for (MetricNameTemplate metricName : templates) {
                metricNames.add(
                        new MetricName(
                                metricName.name(),
                                metricName.group(),
                                metricName.description(),
                                new HashMap<String, String>() {{
                                    put("client-id", clientIdTaskNodeId._1);
                                    put("task-id", clientIdTaskNodeId._2);
                                    put("processor-node-id", clientIdTaskNodeId._3);
                                }}
                        )
                );
            }
        }
        return metricNames;
    }
}
