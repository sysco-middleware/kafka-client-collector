package no.sysco.middleware.prometheus.kafka.internal;

import org.apache.kafka.common.MetricName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// example;
// MetricName [name=version, group=app-info, description=Metric indicating version, tags={client-id=ce3a8395-94b0-4196-811e-9167eb728b8a}]
public class AppInfoMetricNames {
    private final static String APP_INFO_METRICS = "app-info";
    private final static String KAFKA_METRICS_COUNT = "kafka-metrics-count";

    public static Set<MetricName> INITIAL_APP_INFO_METRIC_NAMES = new HashSet<>(
            Arrays.asList(
                    new MetricName(
                            "version",
                            APP_INFO_METRICS,
                            "Metric indicating version",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "commit-id",
                            APP_INFO_METRICS,
                            "Metric indicating commit-id",
                            new HashMap<>()
                    ),
                    new MetricName(
                            "count",
                            APP_INFO_METRICS,
                            "total number of registered metrics",
                            new HashMap<>()
                    )

            ));

    public static Set<MetricName> getAppInfoMetricNames(Set<String> clientIds) {
        Set<MetricName> metricNames = new HashSet<>();
        for (String clientId : clientIds) {
            for (MetricName metricName : INITIAL_APP_INFO_METRIC_NAMES) {
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
