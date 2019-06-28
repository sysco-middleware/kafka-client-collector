package no.sysco.middleware.prometheus.kafka.example;

import io.prometheus.client.exporter.HTTPServer;
import no.sysco.middleware.prometheus.kafka.KafkaClientsJmxExports;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class Streams {
    public static void main(String[] args) throws IOException {
        String id1 = UUID.randomUUID().toString();
        final String topic = "topic-in";

        HTTPServer server = new HTTPServer(8083);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(topic, Consumed.with(Serdes.String(), Serdes.String()))
                .foreach((k, v) -> System.out.println(k + " : "+v));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), getConsumerProps(id1));

        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        System.out.println(Arrays.asList(platformMBeanServer.getDomains()));

        Map<MetricName, ? extends Metric> metrics = kafkaStreams.metrics();
        for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
            System.out.println(entry.getKey());
            KafkaMetric value = (KafkaMetric) entry.getValue();
            System.out.println(value.metricValue());
            System.out.println();
        }

        KafkaClientsJmxExports.initialize(metrics.keySet());
        kafkaStreams.start();

    }

    static Properties getConsumerProps(String id) {
        final Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, id+1);
        properties.put(StreamsConfig.CLIENT_ID_CONFIG, id);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }
}
