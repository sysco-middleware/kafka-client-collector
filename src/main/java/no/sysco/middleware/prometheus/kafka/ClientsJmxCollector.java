package no.sysco.middleware.prometheus.kafka;

import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.clients.ProducerJmxCollector;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.internal.ProducerMetricsTemplates;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.stream.Collectors;

//todo: doc
public class ClientsJmxCollector extends Collector {

    private final static List<String> KAFKA_CLIENTS_DOMAINS = Arrays.asList(
            ProducerMetricsTemplates.PRODUCER_DOMAIN
    );

    private List<KafkaClientJmxCollector> kafkaClientJmxCollectors;

    ClientsJmxCollector() {
        this(ManagementFactory.getPlatformMBeanServer());
    }

    private ClientsJmxCollector(MBeanServer mBeanServer) {
        Map<String, Boolean> kafkaDomainFound = findKafkaDomains(mBeanServer.getDomains());
        this.kafkaClientJmxCollectors = instantiateCollectors(kafkaDomainFound);
    }

    private Map<String, Boolean> findKafkaDomains(String[] domains) {
        Map<String, Boolean> map = new HashMap<>();
        List<String> beanDomains = Arrays.asList(domains);
        for (String kafkaDomain : KAFKA_CLIENTS_DOMAINS) {
            if (beanDomains.contains(kafkaDomain)){
                map.put(kafkaDomain, true);
            } else {
                map.put(kafkaDomain, false);
            }
        }
        return map;
    }

    private List<KafkaClientJmxCollector> instantiateCollectors(Map<String, Boolean> kafkaDomainFound) {
        List<KafkaClientJmxCollector> collectors = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : kafkaDomainFound.entrySet()) {
            if (entry.getValue()){
                String domain = entry.getKey();
                if (ProducerMetricsTemplates.PRODUCER_DOMAIN.equals(domain)) {
                    collectors.add(new ProducerJmxCollector());
                }
            }
        }
        return collectors;
    }

    public List<MetricFamilySamples> collect() {
        return kafkaClientJmxCollectors.stream()
                .flatMap(collector -> collector.getMetrics().stream())
                .collect(Collectors.toList());
    }
}
