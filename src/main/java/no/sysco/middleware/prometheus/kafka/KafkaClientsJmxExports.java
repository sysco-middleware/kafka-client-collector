package no.sysco.middleware.prometheus.kafka;

public class KafkaClientsJmxExports {
    private static boolean initialized = false;

    public static synchronized void initialize() {
        if (!initialized) {
            new KafkaClientsJmxCollector().register();
            initialized = true;
        }
    }
}
