package no.sysco.middleware.prometheus.kafka.domain;

import java.util.Arrays;
import java.util.List;

/**
 * standard JMX MBean name in the following format domainName:type=metricType,key1=val1,key2=val2
 * example:
 *  "kafka.producer:type=producer-metrics,client-id=*"
 *
 *  //todo:
 *  Supported metric groups:
 *   - producer-metrics
 *  producer-metrics
 * */

public class KafkaClientsDomain {
    public final static String KAFKA_PRODUCER_DOMAIN = "kafka.producer";
    public final static String KAFKA_CONSUMER_DOMAIN = "kafka.consumer";
    public final static String KAFKA_ADMIN_CLIENT_DOMAIN = "kafka.admin.client";
    public final static String KAFKA_STREAMS_DOMAIN = "kafka.streams";
    public final static List<String> KAFKA_CLIENTS_DOMAINS = Arrays.asList(
            KAFKA_PRODUCER_DOMAIN,
            KAFKA_CONSUMER_DOMAIN,
            KAFKA_ADMIN_CLIENT_DOMAIN,
            KAFKA_STREAMS_DOMAIN
    );
}
