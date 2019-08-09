[![Build Status](https://www.travis-ci.org/sysco-middleware/kafka-client-collector.svg?branch=master)](https://www.travis-ci.org/sysco-middleware/kafka-client-collector)
[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/no/sysco/middleware/prometheus/kafka-client-collector/maven-metadata.xml.svg)](https://repo1.maven.org/maven2/no/sysco/middleware/prometheus/kafka-client-collector)

# Kafka client collector
Kafka client collector is an implementation of [Prometheus custom collector](https://github.com/prometheus/client_java#custom-collectors), 
for collecting JMX metrics from kafka clients.

## Usage 
```xml
<dependency>
    <groupId>no.sysco.middleware.prometheus</groupId>
    <artifactId>kafka-client-collector</artifactId>
    <version>0.0.2</version>
</dependency>
```

### Prometheus http server
Use `KafkaClientsJmxExports` to initialize collectors for kafka client's JMX metrics to conveniently register them.
```java
HTTPServer server = new HTTPServer(8081);
Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

KafkaClientsJmxExports.initialize(kafkaProducer1);
```

### Armeria http server
Add armeria dependency, than 
```java
/** init health-check, config, metrics */
final CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
new ClientsJmxCollector(kafkaProducer).register(collectorRegistry);

Server server = new ServerBuilder().http(8085)
    .service("/", (ctx, req) -> HttpResponse.of("OK"))
    .service("/config", (ctx, req) -> HttpResponse.of(appConfig.properties.toString()))
    .service("/metrics", (ctx, req) -> {
      final ByteArrayOutputStream stream = new ByteArrayOutputStream();
      try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
        TextFormat.write004(writer, collectorRegistry.metricFamilySamples());
      }
      return HttpResponse.of(HttpStatus.OK, CONTENT_TYPE_004, stream.toByteArray());
    })
    .build();
CompletableFuture<Void> future = server.start();
// Wait until the server is ready.
future.join();
```

Follow [full example](https://github.com/sysco-middleware/kafka-client-collector-examples/blob/master/src/main/java/no/sysco/middleware/prometheus/kafka/armeria/Application.java)
## Metric format
```
JMX example
kafka.producer:type=producer-metrics,client-id="dasf-gdfgd-dfgd-31",waiting-threads="5"

Will be exposed as
producer_metrics_waiting_threads {cliend-id="dasf-gdfgd-dfgd-31"} 5.0
```
JMX domain (kafka.producer - example above) is not present in Prometheus format.

## Metrics types (group) 
### Producer:
* `app-info` @deprecated 
* `producer-metrics` 
* `producer-topic-metrics`  
* `producer-node-metrics`
### Consumer:
* `app-info` @deprecated 
* `consumer-metrics` 
* `consumer-coordinator-metrics`
* `consumer-fetch-manager-metrics` 
* `consumer-node-metrics`
### Stream:
Stream contains metrics from domains `kafka.producer`, `kafka.consumer`, `kafka.admin.client` and own set of metrics
such as :
* `app-info` @deprecated
* `stream-metrics` [INFO lvl]  
* `stream-task-metrics` [DEBUG lvl]
* `stream-processor-node-metrics` [DEBUG lvl]
* `stream-[store-scope]-metrics` [DEBUG lvl]
* `stream-record-cache-metrics` [DEBUG lvl]
* `stream-buffer-metrics` [DEBUG lvl]

### References
- [More examples](https://github.com/sysco-middleware/kafka-client-collector-examples)
- [Issue 305: Add kafka client example config](https://github.com/prometheus/jmx_exporter/pull/305#issuecomment-412851484)
- [Issue 400: does the client automatically publish jmx mertices (heap size memory, thread number)?](https://github.com/prometheus/client_java/issues/400)
- [Blog post: JMX monitoring + Java custom metrics.](https://sysdig.com/blog/jmx-monitoring-custom-metrics/)
- [Kafka metrics reporter](https://github.com/apache/kafka/blob/2.0.0/clients/src/main/java/org/apache/kafka/common/metrics/MetricsReporter.java)
- [IMO - Good example how to make custom collector](https://github.com/joyent/manta-monitor/blob/master/src/main/java/com/joyent/manta/monitor/CustomPrometheusCollector.java)
- Approaches (POCs) with `org.apache.kafka.common.metrics.MetricReporter`
    - [kafka-prometheus-metric-reporter](https://github.com/ripa1993/kafka-prometheus-metric-reporter)
    - [kafka-client-monitoring-poc](https://github.com/sysco-middleware/kafka-client-monitoring-poc)
- [Micrometer [PR WIP]](https://github.com/micrometer-metrics/micrometer/pull/1173/files)
