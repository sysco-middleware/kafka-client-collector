# 25.06.19
Build draft implementation based on idea having dependency injection(di) of kafka-client metrics.
Iterate within this metrics and get values from kafka related beans which are registered in MBeanServer.
```java
final KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(getProducerProps(id1));
Set<MetricName> metrics = kafkaProducer1.metrics().keySet();
// di
KafkaClientsJmxExports.initialize(metrics);
```  

`Update`: Idea implemented in branch `feature/draft`

# 01.07.19
Build draft implementation without having dependency injection. 
```java
KafkaClientsJmxExports.initialize();
```
Ideas: 
* Register all kafka-client metrics
```java
//example
// registered in MBeanServer as `kafka.producer:type=producer-metrics,client-id=2312-123432-5345`
new MetricName("record-send-rate", PRODUCER_METRICS, "The average number of records sent per second.",new HashMap<>())
``` 
* Keep in mind that one java process can have several kafka-clients, which can be identified by `client-id`

# 02.07.19
## Producer metrics 
Producer has 2 metric group : 
* "producer-metrics" - has tags "client-id", which can be initialized at startup of application
* "producer-topic-metrics" - has tags "client-id", "topic". Because of "topic", can be initialize dynamically at run-time.

What is the best way to collect Metrics for second group.   

# 04.07.19
New iteration of dev process lead me finally to apache kafka docs: https://kafka.apache.org/documentation/#selector_monitoring 
There are producer metrics groups:

| group                           | clients       | initialised at    |
| -------------                   |:-------------:|             -----:|
| `app-info`                      | all           | startup           |
| `<producer>-node-metrics`       | all           | runtime           |
| `producer-metrics`              | producer      | startup + runtime |
| `producer-topic-metrics`        | producer      | runtime           |

Naming become a nightmare. 

todos:
* check https://github.com/micrometer-metrics/micrometer 

Thread-off:
Make Set<MetricName> init dynamic at runtime or separate some at startup time, some at runtime;
Benefit is performance. 
  
# 09.07.19
I decided to initialise all metric names (`Set<MetricName>`) at runtime.
Sequence:
```
// init kafka client:
// new KafkaStreams(...);
// new KafkaProducer();
// new kafkaConsumer();
// ...
KafkaClientsJmxExports.initialize();
application.start();
``` 
Kafka clients should be initialised before collecting JMX metrics. 
Because custom collector will collect kafka-client ids.

TODO: create validation mechanism for checking attribute exist.  
 
Example:
```
Exception in thread "main" java.lang.IllegalArgumentException: The specified attribute does not exist or cannot be retrieved: reauthentication-latency-max
	at no.sysco.middleware.prometheus.kafka.KafkaClientJmxCollector.getMBeanAttributeValue(KafkaClientJmxCollector.java:133)
	at no.sysco.middleware.prometheus.kafka.KafkaClientJmxCollector.getMetricsPerClient(KafkaClientJmxCollector.java:154)
	at no.sysco.middleware.prometheus.kafka.clients.ConsumerJmxCollector.getAllMetrics(ConsumerJmxCollector.java:77)
	at no.sysco.middleware.prometheus.kafka.ClientsJmxCollector.lambda$collect$0(ClientsJmxCollector.java:63)
	at java.util.stream.ReferencePipeline$7$1.accept(ReferencePipeline.java:267)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
	at no.sysco.middleware.prometheus.kafka.ClientsJmxCollector.collect(ClientsJmxCollector.java:64)
	at io.prometheus.client.CollectorRegistry.collectorNames(CollectorRegistry.java:100)
	at io.prometheus.client.CollectorRegistry.register(CollectorRegistry.java:50)
	at io.prometheus.client.Collector.register(Collector.java:139)
	at io.prometheus.client.Collector.register(Collector.java:132)
	at no.sysco.middleware.prometheus.kafka.KafkaClientsJmxExports.initialize(KafkaClientsJmxExports.java:8)
	at no.sysco.middleware.prometheus.kafka.example.Consumer.main(Consumer.java:32)
```

# 12.07.19
1. Add `try-catch` construct and `log.warn` when call `getMBeanAttributeValue` and log warn any IllegalArgument 

# 19.07.19
TODOs: 
1. Naming consistency
2. refactoring
3. Logging
