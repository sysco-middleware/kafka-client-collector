package no.sysco.middleware.prometheus.kafka;

import org.apache.kafka.common.MetricName;

import java.util.Set;

public class KafkaClientsJmxExports {
    private static boolean initialized = false;

    public static synchronized void initialize(Set<MetricName> metricNameSet) {
        if (!initialized) {
            new ClientsJmxCollector(metricNameSet).register();
            initialized = true;
        }
    }
}
