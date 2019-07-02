package no.sysco.middleware.prometheus.kafka.common;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import org.apache.kafka.common.MetricName;

import javax.management.*;
import java.util.*;

// todo: doc
// todo: add support for metric group `app-info`
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
    public ObjectName getObjectName(final String metricType, final String key, final String val) {
        String objectNameWithDomain = domainName + ":type=" + metricType + "," + key + "=" + val;
//        System.out.println(objectNameWithDomain);
        ObjectName responseObjectName = null;
        try {
            ObjectName mbeanObjectName = new ObjectName(objectNameWithDomain);
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
    public <T extends Number> T getMBeanAttributeValue(final String metricType, final String attribute, final String key, final String val, final Class<T> returnType) {
        System.out.println(String.format("domainName:%s ; metricType:%s ; attribute:%s ; key:%s ; val:%s" , domainName, metricType, attribute, key, val));

        ObjectName objectName = getObjectName(metricType, key, val);
        if (objectName == null) {
            String message = "Requested MBean Object not found";
            throw new IllegalArgumentException(message);
        }

        Object value;
        try {
            System.out.println("object name: " + objectName + ", attribute: " + attribute);
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

            if (returnType.equals(number.getClass())) {
                return (T) number;
            } else if (returnType.equals(Short.class)) {
                return (T) Short.valueOf(number.shortValue());
            } else if (returnType.equals(Integer.class)) {
                return (T) Integer.valueOf(number.intValue());
            } else if (returnType.equals(Long.class)) {
                return (T) Long.valueOf(number.longValue());
            } else if (returnType.equals(Float.class)) {
                return (T) Float.valueOf(number.floatValue());
            } else if (returnType.equals(Double.class)) {
                return (T) Double.valueOf(number.doubleValue());
            } else if (returnType.equals(Byte.class)) {
                return (T) Byte.valueOf(number.byteValue());
            }
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
        return null;
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
                    getMBeanAttributeValue(metricType, metricName.name(),"client-id", clientId, Double.class)
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
    public abstract List<Collector.MetricFamilySamples> getMetrics();

}
