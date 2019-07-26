package no.sysco.middleware.prometheus.kafka.example;

import io.prometheus.client.exporter.HTTPServer;
import no.sysco.middleware.prometheus.kafka.KafkaClientsJmxExports;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Produced;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;

public class Stream {

    public static void main(String[] args) throws InterruptedException, IOException {
        final String topic1 = "topic-1";
        final String topic2 = "topic-2";
        String id = UUID.randomUUID().toString();
        final Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-metrics");
        properties.put(StreamsConfig.CLIENT_ID_CONFIG, id);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());




        KafkaStreams kafkaStreams = new KafkaStreams(buildTopology(topic1, topic2), properties);
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        KafkaStreams.State state = kafkaStreams.state();

        // http server
        HTTPServer server = new HTTPServer(8080);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        // register metrics
        KafkaClientsJmxExports.initialize();

        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        List<String> domains = Arrays.asList(platformMBeanServer.getDomains());
        System.out.println("HERE: "+domains);

        while (state.isRunning()) {
            printMetrics(kafkaStreams.metrics());
            Thread.sleep(3000L);
        }
    }


    static void printMetrics(Map<MetricName, ? extends Metric> metrics) {
        for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
            System.out.println(entry.getKey());
            KafkaMetric value = (KafkaMetric) entry.getValue();
            System.out.println(value.metricValue());
            System.out.println();
        }
    }

    static Topology buildTopology(String in, String out) {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        streamsBuilder.stream(in)
                .mapValues(v -> {
                    System.out.printf("Value [%s]\n", v);
                    return v+" new value";
                })
                .to(out);

        return streamsBuilder.build();
    }
}
