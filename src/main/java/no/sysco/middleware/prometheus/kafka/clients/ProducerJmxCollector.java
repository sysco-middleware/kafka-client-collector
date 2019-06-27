package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import org.apache.kafka.common.MetricName;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// producer-metrics
public class ProducerJmxCollector extends KafkaClientJmxCollector {
    public static String DOMAIN_NAME = "kafka.producer";
    public static String METRIC_TYPE = "producer-metrics";
    private Set<MetricName> metricNames;

    private ProducerJmxCollector(Set<MetricName> allMetricNames, MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.metricNames = allMetricNames.stream().filter(metric -> METRIC_TYPE.equals(metric.group())).collect(Collectors.toSet());
    }

    public ProducerJmxCollector(Set<MetricName> metricNames) {
        this(
                metricNames,
                ManagementFactory.getPlatformMBeanServer(),
                DOMAIN_NAME
        );
    }

    /**
     *  JMX         kafka.consumer:type=consumer-metrics,request-size-avg=45,client-id=2c980848-6a12-4718-a473-79c6d195e3e6,
     *                      |                   |                   |                       |
     *                      |                   |                   |                       |
     *  BEAN             domain             objectName          objectName              objectName
     *                                          |                   |
     *                                          |                   |
     *  MetricName                      metricName.group()     metricName.name()
     *                                          |                   |
     *                                          |                   |
     *  MetricFamilySamples           format(metricName)    metricName.description()
     * */
    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        List<Collector.MetricFamilySamples> mfs = new ArrayList<>();
        for (MetricName metricName : metricNames) {
            String id = metricName.tags().get("client-id");
            if (METRIC_TYPE.contains(metricName.group())) {
                // todo: support counters, all metric.name() which contains `-total`
                GaugeMetricFamily gaugeMetricFamily = new GaugeMetricFamily(
                        formatMetricName(metricName),
                        metricName.description(),
                        Collections.singletonList("id"));
                gaugeMetricFamily.addMetric(
                        Collections.singletonList(id),
                        getMBeanAttributeValue(METRIC_TYPE, metricName.name(), id, Double.class));
                mfs.add(gaugeMetricFamily);
            }
        }
        return mfs;
    }


}
