package no.sysco.middleware.prometheus.kafka.clients;


import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.internal.ProducerMetricsTemplates;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.stream.Collectors;

// producer-metrics
// Domains will look like :
// [JMImplementation, java.util.logging, java.lang, com.sun.management, kafka.producer, java.nio]
public class ProducerJmxCollector extends KafkaClientJmxCollector {

    private final Set<MetricName> producerMetricNames;
    private ProducerMetricsTemplates producerMetricsTemplates;

    private ProducerJmxCollector(MBeanServer mBeanServer, String domainName) {
        super(mBeanServer, domainName);
        this.producerMetricsTemplates = new ProducerMetricsTemplates();
        this.producerMetricNames = initProducerMetricNamesWithClientId();
    }

    public ProducerJmxCollector() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                ProducerMetricsTemplates.PRODUCER_DOMAIN
        );
    }

    private Set<MetricName> initProducerMetricNamesWithClientId() {
        Set<String> kafkaClientIds = getKafkaClientIds(ProducerMetricsTemplates.PRODUCER_METRIC_GROUP_NAME);
        return producerMetricsTemplates.getMetricNamesPerClientId(kafkaClientIds);
    }

    // todo: add tags at runtime. Such as client-id, node-id, etc. . .
    protected Set<String> getKafkaTopics() {
        String objectNameWithDomain = ProducerMetricsTemplates.PRODUCER_DOMAIN  + ":type=" + ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME + ",*";
        try {
            ObjectName mbeanObjectName = new ObjectName(objectNameWithDomain);
            Set<ObjectName> objectNamesFromString = mBeanServer.queryNames(mbeanObjectName, null);

            Set<String> topics = new HashSet<>();
            for (ObjectName objectName : objectNamesFromString) {
                topics.add(objectName.getKeyProperty("topic"));
            }
            return topics;
        } catch (MalformedObjectNameException mfe) {
            throw new IllegalArgumentException(mfe.getMessage());
        }
    }

    @Override
    public List<Collector.MetricFamilySamples> getMetrics() {
        Set<String> kafkaClientIds = getKafkaClientIds(ProducerMetricsTemplates.PRODUCER_METRIC_GROUP_NAME);
        System.out.println("IDS "+kafkaClientIds);
//        printObjectNames(getKafkaTopics());
        printObjectNames(kafkaClientIds);

        List<String> collect = new ArrayList<>(getKafkaTopics());
        Iterator<String> iterator = collect.iterator();

        while (iterator.hasNext()) {
            String topic = iterator.next();
//            Double value = getValue(ProducerMetricsTemplates.PRODUCER_METRIC_GROUP_NAME, "record-send-rate", "topic", topic);
//            System.out.println(value);
//            System.out.println("HH "+topic);
//            ObjectName topic1 = getObjectName(ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME, "topic", topic);
//            Double value = getMBeanAttributeValue(ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME, "byte-total", "topic", topic, Double.class);
//            System.out.println("WAAAT "+ topic + " : "+ value);
//
//            ObjectName objectNameFromString = getObjectName(ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME,"topic", topic);
//            Hashtable<String, String> keyPropertyList = objectNameFromString.getKeyPropertyList();
//            System.out.println("HERE --> "+ keyPropertyList);
        }
        return getMetrics(ProducerMetricsTemplates.PRODUCER_METRIC_GROUP_NAME, producerMetricNames);
    }

    public Double getValue(final String attribute, final String clientId, final String topic) {
//        System.out.println(String.format("domainName:%s ; metricType:%s ; attribute:%s ; key:%s ; val:%s" , domainName, metricType, attribute, key, val));

        ObjectName objectName = getObjectNameClientTopic(ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME, clientId, topic);
        System.out.println("TUT "+ objectName);

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

    public ObjectName getObjectNameClientTopic(final String metricType, final String clientId, final String topic) {
        String objectNameWithDomain =
                ProducerMetricsTemplates.PRODUCER_DOMAIN + ":type=" +
                ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME +
                ",client-id=" + clientId + ",topic=" + topic;
        ObjectName responseObjectName = null;
        try {
            ObjectName mbeanObjectName = new ObjectName(objectNameWithDomain);
            Set<ObjectName> objectNames = mBeanServer.queryNames(mbeanObjectName, null);
            for (ObjectName object : objectNames) {
                System.out.println("FOUND "+ object);
                responseObjectName = object;
            }
        } catch (MalformedObjectNameException mfe) {
            throw new IllegalArgumentException(mfe.getMessage());
        }
        return responseObjectName;
    }



    // kafka.producer:type=producer-topic-metrics,client-id=25862c0e-9da0-48f7-82b5-cdf077d1ff6a,topic=topic-2
    public Map<String, Set<String>> getTopicsPerClient(final String clientId) {
        String objectNameWithDomain =
                ProducerMetricsTemplates.PRODUCER_DOMAIN  +
                        ":type=" + ProducerMetricsTemplates.PRODUCER_TOPIC_METRIC_GROUP_NAME +
                        ",client-id=" + clientId + ",*";
        try {
            Map<String, Set<String>> clientWithTopics = new HashMap<>();
            ObjectName mbeanObjectName = new ObjectName(objectNameWithDomain);
            Set<ObjectName> objectNamesFromString = mBeanServer.queryNames(mbeanObjectName, null);
            for (ObjectName objectName : objectNamesFromString) {
                String id = objectName.getKeyProperty("client-id");
                String topicName = objectName.getKeyProperty("topic");
                Set<String> topicList = clientWithTopics.getOrDefault(id, new HashSet<String>());
                topicList.add(topicName);
                clientWithTopics.put(id, topicList);
            }
            return clientWithTopics;
        } catch (MalformedObjectNameException mfe) {
            throw new IllegalArgumentException(mfe.getMessage());
        }
    }

    public void printObjectNames(Set<String> ids) {
        for (String id : ids) {
            Map<String, Set<String>> topicsPerClient = getTopicsPerClient(id);
//            System.out.println("HERE::: "+topicsPerClient);
            for (Map.Entry<String, Set<String>> entry :topicsPerClient.entrySet()){
                for (String topic : entry.getValue()) {
                    System.out.println("VALUE " + getValue("record-send-rate", entry.getKey(), topic));
                }
            }
        }
    }



}
