package no.sysco.middleware.prometheus.kafka;

import io.prometheus.client.Collector;
import no.sysco.middleware.prometheus.kafka.clients.AdminClientJmxCollector;
import no.sysco.middleware.prometheus.kafka.clients.ConsumerJmxCollector;
import no.sysco.middleware.prometheus.kafka.clients.ProducerJmxCollector;
import no.sysco.middleware.prometheus.kafka.clients.StreamsJmxCollector;
import no.sysco.middleware.prometheus.kafka.common.KafkaClientJmxCollector;
import org.apache.kafka.common.MetricName;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.stream.Collectors;

//todo: doc
public class ClientsJmxCollector extends Collector {

    private final static List<String> KAFKA_CLIENTS_DOMAINS = Arrays.asList(
            ProducerJmxCollector.DOMAIN_NAME,
            ConsumerJmxCollector.DOMAIN_NAME,
            AdminClientJmxCollector.DOMAIN_NAME,
            StreamsJmxCollector.DOMAIN_NAME
    );

    private List<KafkaClientJmxCollector> kafkaClientJmxCollectors;

    ClientsJmxCollector(Set<MetricName> allMetricNames) {
        this(allMetricNames, ManagementFactory.getPlatformMBeanServer());
    }

    private ClientsJmxCollector(Set<MetricName> allMetricNames, MBeanServer mBeanServer) {
        Map<String, Boolean> kafkaDomainFound = findKafkaDomains(mBeanServer.getDomains());
        this.kafkaClientJmxCollectors = instantiateCollectors(kafkaDomainFound, allMetricNames);
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

    private List<KafkaClientJmxCollector> instantiateCollectors(Map<String, Boolean> kafkaDomainFound, Set<MetricName> allMetricNames) {
        List<KafkaClientJmxCollector> collectors = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : kafkaDomainFound.entrySet()) {
            if (entry.getValue()){
                String domain = entry.getKey();
                if (ProducerJmxCollector.DOMAIN_NAME.equals(domain)) {
                    collectors.add(new ProducerJmxCollector(allMetricNames));
                } else if(ConsumerJmxCollector.DOMAIN_NAME.equals(domain)) {
                    collectors.add(new ConsumerJmxCollector(allMetricNames));
                } else if (AdminClientJmxCollector.DOMAIN_NAME.equals(domain)) {
                    collectors.add(new AdminClientJmxCollector(allMetricNames));
                } else if (StreamsJmxCollector.DOMAIN_NAME.equals(domain)) {
                    collectors.add(new StreamsJmxCollector(allMetricNames));
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
