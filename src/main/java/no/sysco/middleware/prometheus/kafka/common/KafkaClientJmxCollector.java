package no.sysco.middleware.prometheus.kafka.common;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KeyValue;

import javax.management.*;
import java.util.*;

// todo: doc
public abstract class KafkaClientJmxCollector {
    protected final MBeanServer mBeanServer;
    protected final String domainName;

    public KafkaClientJmxCollector(MBeanServer mBeanServer, String domainName) {
        this.mBeanServer = mBeanServer;
        this.domainName = domainName;
    }

    protected Set<String> getKafkaClientIds(String metricType) {
        String objectNameWithDomain = domainName + ":type=" + metricType + ",*";
        try {
            ObjectName mbeanObjectName = new ObjectName(objectNameWithDomain);
            Set<ObjectName> objectNamesFromString = mBeanServer.queryNames(mbeanObjectName, null);

            Set<String> ids = new HashSet<>();
            for (ObjectName objectName : objectNamesFromString) {
                ids.add(objectName.getKeyProperty("client-id"));
            }
            return ids;
        } catch (MalformedObjectNameException mfe) {
            throw new IllegalArgumentException(mfe.getMessage());
        }
    }

    /**
     * standard JMX MBean name in the following format domainName:type=metricType,key1=val1,key2=val2
     * example:
     * String objectNameWithDomain = "kafka.producer" + ":type=" + "producer-metrics" + ",client-id="+clientId;
     */
    public ObjectName getObjectName(final String metricType, final KeyValue<String, String>... keyVals) {
        String startQuery = String.format("%s:type=%s", domainName, metricType);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startQuery);
        for (KeyValue<String, String> keyVal : keyVals) {
            String queryContinue = String.format(",%s=%s", keyVal.key, keyVal.value);
            stringBuilder.append(queryContinue);
        }
        String query = stringBuilder.toString();
        ObjectName responseObjectName = null;
        try {
            ObjectName mbeanObjectName = new ObjectName(query);
            Set<ObjectName> objectNames = mBeanServer.queryNames(mbeanObjectName, null);
            for (ObjectName object : objectNames) {
                responseObjectName = object;
            }
        } catch (MalformedObjectNameException mfe) {
            throw new IllegalArgumentException(mfe.getMessage());
        }
        return responseObjectName;
    }

    @SuppressWarnings("unchecked")
    public Double getMBeanAttributeValue(final String metricType, final String attribute, final KeyValue<String, String>... keyValues) {
        ObjectName objectName = getObjectName(metricType, keyValues);
        if (objectName == null) {
            String message = "Requested MBean Object not found";
            throw new IllegalArgumentException(message);
        }

        Object value;
        try {
            value = mBeanServer.getAttribute(objectName, attribute);
            final Number number;

            if (value instanceof Number) {
                number = (Number) value;
            } else {
                try {
                    number = Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    String message = "Failed to parse attribute value to number: " + e.getMessage();
                    throw new IllegalArgumentException(message);
                }
            }

            return number.doubleValue();

        } catch (AttributeNotFoundException | InstanceNotFoundException | ReflectionException | MBeanException e) {
            String message;
            if (e instanceof AttributeNotFoundException) {
                message = "The specified attribute does not exist or cannot be retrieved";
            } else if (e instanceof InstanceNotFoundException) {
                message = "The specified MBean does not exist in the repository.";
            } else if (e instanceof MBeanException) {
                message = "Failed to retrieve the attribute from the MBean Server.";
            } else {
                message = "The requested operation is not supported by the MBean Server ";
            }
            throw new IllegalArgumentException(message);
        }
    }

    public String formatMetricName(final MetricName metricName) {
        String groupName = metricName.group().replace("-", "_");
        String name = metricName.name().replace("-", "_");
        return groupName + "_" + name;
    }

    public List<Collector.MetricFamilySamples> getMetrics(final String metricType, final Set<MetricName> metricNames) {
        List<Collector.MetricFamilySamples> metricFamilySamples = new ArrayList<>();
        for (MetricName metricName : metricNames) {
            String clientId = metricName.tags().get("client-id");
            GaugeMetricFamily gaugeMetricFamily = new GaugeMetricFamily(
                    formatMetricName(metricName),
                    metricName.description(),
                    Collections.singletonList("client-id")
            );
            gaugeMetricFamily.addMetric(
                    Collections.singletonList(clientId),
                    getMBeanAttributeValue(metricType, metricName.name(), KeyValue.pair("client-id", clientId))
            );
            metricFamilySamples.add(gaugeMetricFamily);
        }
        return metricFamilySamples;
    }

    //
//    /**
//     *  JMX         kafka.consumer:type=consumer-metrics,request-size-avg=45,client-id=2c980848-6a12-4718-a473-79c6d195e3e6,
//     *                      |                   |                   |                       |
//     *                      |                   |                   |                       |
//     *  BEAN             domain             objectName          objectName              objectName
//     *                                          |                   |
//     *                                          |                   |
//     *  MetricName                      metricName.group()     metricName.name()
//     *                                          |                   |
//     *                                          |                   |
//     *  MetricFamilySamples           format(metricName)    metricName.description()
//     * */
    public abstract List<Collector.MetricFamilySamples> getAllMetrics();

}
