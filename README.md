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
KafkaClientsJmxExports.initialize(kafkaProducer);
```


## Idea
Provide availability to expose:
- kafka clients jmx related metrics ([KafkaClientsJmxExports.initialize();](./src/main/java/no/sysco/middleware/prometheus/kafka/KafkaClientsJmxExports.java))
- custom metrics (developer's responsibility)
- resource utilization metrics ([DefaultExports.initialize();](https://github.com/prometheus/client_java/blob/master/simpleclient_hotspot/src/main/java/io/prometheus/client/hotspot/DefaultExports.java))

## Keep in mind
- Some of metrics are registered when client initialized
- Some of metrics are registered when client start communicate with kafka cluster   
- Some of metrics are registered when client communicates with specific (f.e. authentication)
- Metrics could be removed
- Application can have several instances of kafka-clients (could be different APIs)
- `Metrics` is registered in JMX via domains depends on which API is in use
- Each domain has own set of metric type (metric's group)
- Stream API is special, because it is register 4 domains (`kafka.streams`, `kafka.consumer`, `kafka.peorducer`, `kafka.admin.client`) and have all set of metric's group from these domains.
- `NB!`: Metrics has INFO and DEBUG log level. Current implementation support only `INFO` level. (@see [Stream](###Stream)) 

## Metrics types
Metrics with Attributes. [Reference](https://github.com/prometheus/jmx_exporter/pull/305/commits/92a6eb106e84cd441ba9b6123132395738d6acd6)
Name of `metrics group` (prometheus context) or `metric type` (jmx context)
### Producer:
* `app-info` @deprecated = common clients metrics
* `producer-metrics` = common clients metrics + only producer related metrics
* `producer-topic-metrics` =  only producer related metrics 
* `producer-node-metrics` = common clients metrics

### Consumer:
* `app-info` @deprecated = common clients metrics
* `consumer-metrics` = common clients metrics
* `consumer-coordinator-metrics` - consumer group metrics
* `consumer-fetch-manager-metrics` =  fetch-manager metrics + per topic + per partition
* `consumer-node-metrics` = common clients metrics

###Stream:
Stream contains metrics from domains `kafka.producer`, `kafka.consumer`, `kafka.admin.client` and own set of metrics
such as :
* `app-info` @deprecated = common clients metrics
* `stream-metrics` = only stream related metrics [INFO lvl]
* `stream-task-metrics` = stream task related metrics [DEBUG lvl]
* `stream-processor-node-metrics` = stream processor related metrics [DEBUG lvl]
* `stream-[store-scope]-metrics` = stream store related metrics [DEBUG lvl]
* `stream-record-cache-metrics` = stream record cache related metrics [DEBUG lvl]
* `stream-buffer-metrics` = stream buffer related metrics [DEBUG lvl]


### References
- [Issue 305: Add kafka client example config](https://github.com/prometheus/jmx_exporter/pull/305#issuecomment-412851484)
- [Issue 400: does the client automatically publish jmx mertices (heap size memory, thread number)?](https://github.com/prometheus/client_java/issues/400)
- [Blog post: JMX monitoring + Java custom metrics.](https://sysdig.com/blog/jmx-monitoring-custom-metrics/)
- [Kafka metrics reporter](https://github.com/apache/kafka/blob/2.0.0/clients/src/main/java/org/apache/kafka/common/metrics/MetricsReporter.java)
- [IMO - Good example how to make custom collector](https://github.com/joyent/manta-monitor/blob/master/src/main/java/com/joyent/manta/monitor/CustomPrometheusCollector.java)
- Approaches (POCs) with `org.apache.kafka.common.metrics.MetricReporter`
    - [kafka-prometheus-metric-reporter](https://github.com/ripa1993/kafka-prometheus-metric-reporter)
    - [kafka-client-monitoring-poc](https://github.com/sysco-middleware/kafka-client-monitoring-poc)
- [Micrometer [PR WIP]](https://github.com/micrometer-metrics/micrometer/pull/1173/files)
