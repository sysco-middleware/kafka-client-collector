package no.sysco.middleware.prometheus.kafka.clients;

import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.template.StreamMetricTemplates;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KeyValue;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamJmxCollector extends KafkaClientJmxCollector {
    private StreamMetricTemplates streamMetricTemplates;
    private Set<String> kafkaClientIds;

    private StreamJmxCollector(MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.streamMetricTemplates = new StreamMetricTemplates();
        this.kafkaClientIds = getKafkaClientIds(StreamMetricTemplates.STREAM_METRIC_GROUP_NAME);
    }

    public StreamJmxCollector() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                StreamMetricTemplates.STREAM_DOMAIN
        );
    }

    List<Collector.MetricFamilySamples> getMetricsStreamTask() {
        // stream-task-metrics
        String metricType = streamMetricTemplates.streamTaskMetricsTemplates.metricGroupName;
        Set<KeyValue<String, String>> clientsTasksList = new HashSet<>();
        for (String id : kafkaClientIds) {
            Set<KeyValue<String, String>> tasksPerClient = getClientTasksSet(StreamMetricTemplates.STREAM_DOMAIN, metricType, id);
            clientsTasksList.addAll(tasksPerClient);
        }
        Set<MetricName> metricsPerClientIdTasks = streamMetricTemplates.getMetricNamesStreamTask(clientsTasksList);
        List<Collector.MetricFamilySamples> perClientIdTasks = getMetricsPerClientIdTasks(metricType, metricsPerClientIdTasks);
        return perClientIdTasks;
    }

    @Override
    public List<Collector.MetricFamilySamples> getAllMetrics() {
        Set<MetricName> metricNamesStreamThread = streamMetricTemplates.getMetricNamesStreamThread(kafkaClientIds);

        // stream-metrics (stream thread metrics)
        List<Collector.MetricFamilySamples> metricsStreamThread =
                getMetricsPerClient(StreamMetricTemplates.STREAM_METRIC_GROUP_NAME, metricNamesStreamThread);
        // stream-task-metrics
        List<Collector.MetricFamilySamples> metricsPerTask = getMetricsStreamTask();

        return Stream
                .of(metricsStreamThread, metricsPerTask)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
