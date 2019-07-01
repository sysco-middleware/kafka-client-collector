# Kafka clients JMX collector [WIP]
Kafka clients JMX collector is [Custom collector](https://github.com/prometheus/client_java#custom-collectors), for [prometheus/client_java](https://github.com/prometheus/client_java#custom-collectors).

`Kafka clients JMX collector` does proxy JMX metrics from kafka clients.

## Versioning 
todo:
0.6.0-2.1.1 -> {prometheus.kafka-clients}

## Usage
Import dependency.
```xml
<dependency>
    <groupId>no.sysco.middleware.prometheus</groupId>
    <artifactId>kafka-client-collector</artifactId>
    <version>${version}</version>
</dependency>
```

Use `KafkaClientsJmxExports` to initialize collectors for kafka-clients JMX metrics to conveniently register them.
```java
Set<MetricName> metricNames = kafkaProducer.metrics().keySet();

KafkaClientsJmxExports.initialize(metricNames);
```

or if you have several instances

```java
Set<MetricName> metricNames1 = kafkaProducer.metrics().keySet();
Set<MetricName> metricNames2 = kafkaConsumer.metrics().keySet();

KafkaClientsJmxExports.initialize(metricNames1, metricNames2);
```

## Idea
Provide availability to expose:
- kafka clients jmx related metrics ([KafkaClientsJmxExports.initialize();](./src/main/java/no/sysco/middleware/prometheus/kafka/KafkaClientsJmxExports.java))
- custom metrics (developer's responsibility)
- resource utilization metrics ([DefaultExports.initialize();](https://github.com/prometheus/client_java/blob/master/simpleclient_hotspot/src/main/java/io/prometheus/client/hotspot/DefaultExports.java))
## Metrics types
Metrics with Attributes. [Reference](https://github.com/prometheus/jmx_exporter/pull/305/commits/92a6eb106e84cd441ba9b6123132395738d6acd6)
```
kafka.admin.client:type=admin-client-metrics,client-id=admin-id-1
kafka.consumer:type=consumer-coordinator-metrics,client-id=consumer-id-1
kafka.consumer:type=consumer-fetch-manager-metrics,client-id=consumer-id-1
kafka.consumer:type=consumer-metrics,client-id=consumer-id-1
kafka.producer:type=producer-metrics,client-id=producer-id-1
kafka.streams:type=stream-metrics,client-id=streams-id-1


kafka.admin.client:type=admin-client-node-metrics,client-id=admin-id-1
kafka.consumer:type=consumer-node-metrics,client-id=consumer-id-1
kafka.producer:type=producer-node-metrics,client-id=producer-id-1

kafka.consumer:type=consumer-fetch-manager-metrics,client-id=moody-test_client-StreamThread-1-consumer,topic=realtime-clustered-documents,partition=92

kafka.streams:type=stream-task-metrics,client-id=moody-test_client-StreamThread-1,task-id=0_92
kafka.streams:type=stream-processor-node-metrics,client-id=moody-test_client-StreamThread-1,task-id=0_100,processor-node-id=KSTREAM-PROCESSOR-0000000001


```

Metrics with Labels
```
kafka.admin.client:type=app-info,client-id=moody-test_client-admin
kafka.consumer:type=app-info,client-id=moody-test_client-StreamThread-1-consumer
kafka.producer:type=app-info,client-id=moody-test_client-StreamThread-1-producer
```

Metrics with count values
```
kafka.admin.client:type=kafka-metrics-count,client-id=moody-test_client-admin
kafka.consumer:type=kafka-metrics-count,client-id=moody-test_client-StreamThread-1-consumer
kafka.producer:type=kafka-metrics-count,client-id=moody-test_client-StreamThread-1-producer
```
### References
- [Issue 305: Add kafka client example config](https://github.com/prometheus/jmx_exporter/pull/305#issuecomment-412851484)
- [Issue 400: does the client automatically publish jmx mertices (heap size memory, thread number)?](https://github.com/prometheus/client_java/issues/400)
- [Blog post: JMX monitoring + Java custom metrics.](https://sysdig.com/blog/jmx-monitoring-custom-metrics/)
- [Kafka metrics reporter](https://github.com/apache/kafka/blob/2.0.0/clients/src/main/java/org/apache/kafka/common/metrics/MetricsReporter.java)
- [IMO - Good example how to make custom collector](https://github.com/joyent/manta-monitor/blob/master/src/main/java/com/joyent/manta/monitor/CustomPrometheusCollector.java)
