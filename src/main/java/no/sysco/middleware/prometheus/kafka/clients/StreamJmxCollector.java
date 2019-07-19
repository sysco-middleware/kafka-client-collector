package no.sysco.middleware.prometheus.kafka.clients;

import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.template.ConsumerMetricTemplates;
import no.sysco.middleware.prometheus.kafka.template.ProducerMetricTemplates;
import no.sysco.middleware.prometheus.kafka.template.StreamMetricTemplates;
import org.apache.kafka.common.MetricName;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Collection;
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


    @Override
    public List<Collector.MetricFamilySamples> getAllMetrics() {
        Set<MetricName> metricNamesStreamThread = streamMetricTemplates.getMetricNamesStreamThread(kafkaClientIds);

        // stream-metrics (stream thread metrics)
        List<Collector.MetricFamilySamples> metricsStreamThread =
                getMetricsPerClient(StreamMetricTemplates.STREAM_METRIC_GROUP_NAME, metricNamesStreamThread);

        return Stream
                .of(metricsStreamThread)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
