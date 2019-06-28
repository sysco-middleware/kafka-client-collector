package no.sysco.middleware.prometheus.kafka.example;

import io.prometheus.client.exporter.HTTPServer;
import no.sysco.middleware.prometheus.kafka.KafkaClientsJmxExports;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.*;

public class AdminClient {
    public static void main(String[] args) throws Exception {

        String id1 = UUID.randomUUID().toString();
        final String topic = "topic-in";

//        DefaultExports.initialize();
        HTTPServer server = new HTTPServer(8082);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        org.apache.kafka.clients.admin.AdminClient adminClient = org.apache.kafka.clients.admin.AdminClient.create(getAdminClientProps(id1));

        for (Map.Entry<MetricName, ? extends Metric> entry : adminClient.metrics().entrySet()) {
            System.out.println(entry.getKey());
            KafkaMetric value = (KafkaMetric) entry.getValue();
            System.out.println(value.metricValue());
            System.out.println();
        }
        Set<MetricName> metrics1 = adminClient.metrics().keySet();
        HashSet<MetricName> metrics = new HashSet<MetricName>() {{
            addAll(metrics1);
        }};
        KafkaClientsJmxExports.initialize(metrics);

        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        System.out.println(Arrays.asList(platformMBeanServer.getDomains()));
    }

    static Properties getAdminClientProps(String id) {
        final Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        properties.put(AdminClientConfig.CLIENT_ID_CONFIG, id);
        return properties;
    }
}
