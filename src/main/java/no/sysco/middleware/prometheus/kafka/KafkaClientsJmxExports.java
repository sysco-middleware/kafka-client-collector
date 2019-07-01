package no.sysco.middleware.prometheus.kafka;

import org.apache.kafka.common.MetricName;

import java.util.HashSet;
import java.util.Set;

public class KafkaClientsJmxExports {
    private static boolean initialized = false;

    public static synchronized void initialize(Set<MetricName> metricNameSet) {
        if (!initialized) {
            new ClientsJmxCollector(metricNameSet).register();
            initialized = true;
        }
    }

    @SafeVarargs
    public static synchronized void initialize(Set<MetricName>... metricNameSets) {
        if (!initialized) {
            HashSet<MetricName> metricNames = new HashSet<>();
            for (Set<MetricName> metricNameSet : metricNameSets) {
                metricNames.addAll(metricNameSet);
            }
            new ClientsJmxCollector(metricNames).register();
            initialized = true;
        }
    }
}
