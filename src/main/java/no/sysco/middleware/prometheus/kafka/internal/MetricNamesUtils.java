package no.sysco.middleware.prometheus.kafka.internal;

import org.apache.kafka.common.MetricName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class MetricNamesUtils {
    public static Set<MetricName> getMetricNamesWithClientId(Set<String> clientIds, Set<MetricName> initialMetricNames) {
        Set<MetricName> metricNames = new HashSet<>();
        for (String clientId : clientIds) {
            for (MetricName metricName : initialMetricNames) {
                metricNames.add(
                        new MetricName(
                                metricName.name(),
                                metricName.group(),
                                metricName.description(),
                                new HashMap<String, String>() {{
                                    put("client-id", clientId);
                                }}
                        )
                );
            }
        }
        return metricNames;
    }
}
