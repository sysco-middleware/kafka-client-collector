package no.sysco.middleware.prometheus.kafka.template.common;

public enum KafkaClient {
    PRODUCER ("producer"),
    CONSUMER ("consumer"),
    CONNECT ("connect"),
    STREAM ("stream");

    private final String name;

    private KafkaClient(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
