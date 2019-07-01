package no.sysco.middleware.prometheus.kafka.clients;


import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.internal.AppInfoMetricNames;
import no.sysco.middleware.prometheus.kafka.internal.ProducerMetricNames;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.MetricName;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

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

    public ProducerJmxCollector() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                PRODUCER_DOMAIN
        );
    }

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
