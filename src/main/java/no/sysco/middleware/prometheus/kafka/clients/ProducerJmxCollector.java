package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.internal.AppInfoMetricNames;
import no.sysco.middleware.prometheus.kafka.internal.ProducerMetricNames;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.MetricName;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.*;

// producer-metrics
// Domains will look like :
// [JMImplementation, java.util.logging, java.lang, com.sun.management, kafka.producer, java.nio]
public class ProducerJmxCollector extends KafkaClientJmxCollector {

    public static String PRODUCER_DOMAIN = "kafka.producer"; // KafkaProducer.JMX_PREFIX
    public static String PRODUCER_METRIC_TYPE = KafkaProducer.PRODUCER_METRIC_GROUP_NAME;
    private final Set<MetricName> producerMetricNames;
    private final Set<MetricName> appInfoMetricNames;

    private ProducerJmxCollector(MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.producerMetricNames = initProducerMetricNamesWithClientId();
        this.appInfoMetricNames = initAppInfoMetricNamesWithProperties();
    }

    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        List<Collector.MetricFamilySamples> metricFamilySamples = new ArrayList<>();
        HashSet<MetricName> allMetricNames = new HashSet<MetricName>() {{
            addAll(producerMetricNames);
            addAll(appInfoMetricNames);
        }};
        for (MetricName metricName : allMetricNames) {
            String clientId = metricName.tags().get("client-id");
            //String nodeId = metricName.tags().get("node-id"); todo;
            if (PRODUCER_METRIC_TYPE.contains(metricName.group())) {
                if (metricName.name().contains("-total")){
                    CounterMetricFamily counterMetricFamily = new CounterMetricFamily(
                            formatMetricName(metricName),
                            metricName.description(),
                            Collections.singletonList("client-id")
                    );
                    counterMetricFamily.addMetric(
                            Collections.singletonList(clientId),
                            getMBeanAttributeValue(PRODUCER_METRIC_TYPE, metricName.name(), clientId, Long.class)
                    );
                    metricFamilySamples.add(counterMetricFamily);
                } else {
                    GaugeMetricFamily gaugeMetricFamily = new GaugeMetricFamily(
                            formatMetricName(metricName),
                            metricName.description(),
                            Collections.singletonList("client-id")
                    );
                    gaugeMetricFamily.addMetric(
                            Collections.singletonList(clientId),
                            getMBeanAttributeValue(PRODUCER_METRIC_TYPE, metricName.name(), clientId, Double.class)
                    );
                    metricFamilySamples.add(gaugeMetricFamily);
                }

            }
        }
        return metricFamilySamples;
    }

    public ProducerJmxCollector() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                PRODUCER_DOMAIN
        );
    }

//
//    public void getAttributeValue(String attribute) {
//        Set<String> kafkaClientIds = getKafkaClientIds();
//        for (String id : kafkaClientIds) {
//            Double mBeanAttributeValue = getMBeanAttributeValue(PRODUCER_METRIC_TYPE, attribute, id, Double.class);
//            System.out.println("GET_ATTRIBUTE +" + attribute + " > " + mBeanAttributeValue);
//        }
//    }

    private Set<String> getKafkaClientIds() {
        Set<ObjectName> objectNamesFromString = getObjectNamesFromString(domainName, PRODUCER_METRIC_TYPE);
        Set<String> ids = new HashSet<>();
        for (ObjectName objectName : objectNamesFromString) {
            ids.add(objectName.getKeyProperty("client-id"));
        }
        return ids;
    }

    // add tags at runtime. Such as client-id, node-id, etc. . .
    private Set<MetricName> initProducerMetricNamesWithClientId() {
        Set<String> kafkaClientIds = getKafkaClientIds();
        Set<MetricName> metricNames = ProducerMetricNames.getMetricNames(kafkaClientIds);
        return metricNames;
    }

    private Set<MetricName> initAppInfoMetricNamesWithProperties() {
        Set<String> kafkaClientIds = getKafkaClientIds();
        Set<MetricName> appInfoMetricNames = AppInfoMetricNames.getAppInfoMetricNames(kafkaClientIds);
        return appInfoMetricNames;
    }

    private Set<ObjectName> getObjectNamesFromString(final String domainName, final String metricType) {
        String objectNameWithDomain = domainName + ":type=" + metricType + ",*";
        try {
            ObjectName mbeanObjectName = new ObjectName(objectNameWithDomain);
            return mBeanServer.queryNames(mbeanObjectName, null);
        } catch (MalformedObjectNameException mfe) {
            throw new IllegalArgumentException(mfe.getMessage());
        }
    }

}
