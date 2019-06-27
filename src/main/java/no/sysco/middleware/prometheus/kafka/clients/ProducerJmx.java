package no.sysco.middleware.prometheus.kafka.clients;


import no.sysco.middleware.prometheus.kafka.common.JmxMetricsCollector;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

public class ProducerJmx extends JmxMetricsCollector {
    public static String DOMAIN = "kafka.producer";
    public static String TYPE = "producer-metrics";


    public ProducerJmx(MBeanServer mBeanServer, String domainName, String type) {
        super(mBeanServer, domainName, type);
    }

    public ProducerJmx() {
        this(
                ManagementFactory.getPlatformMBeanServer(),
                DOMAIN,
                TYPE
        );
    }





}
