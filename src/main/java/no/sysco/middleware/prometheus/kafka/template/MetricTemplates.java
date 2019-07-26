package no.sysco.middleware.prometheus.kafka.template;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

abstract class MetricTemplates {
    /**
     * Get a subset of MetricName per clientId
     * Subset could be initialised at startup of application
     */
    Set<MetricName> getMetricNamesPerClientId(Set<String> clientIdSet, Set<MetricNameTemplate> metricNameTemplates) {
        Set<MetricName> metricNames = new HashSet<>();
        for (String clientId : clientIdSet) {
            for (MetricNameTemplate metricName : metricNameTemplates) {
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
