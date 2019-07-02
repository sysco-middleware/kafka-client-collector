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