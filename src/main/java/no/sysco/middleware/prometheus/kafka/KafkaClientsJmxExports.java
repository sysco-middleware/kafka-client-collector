package no.sysco.middleware.prometheus.kafka;

public class KafkaClientsJmxExports {
  private static boolean initialized = false;

  public static synchronized void initialize(Object... clients) {
    if (!initialized) {
      new ClientsJmxCollector(clients).register();
      initialized = true;
    }
  }
}
