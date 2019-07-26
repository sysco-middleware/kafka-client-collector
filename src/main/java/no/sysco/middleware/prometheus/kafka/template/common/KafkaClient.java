package no.sysco.middleware.prometheus.kafka.template.common;

public enum KafkaClient {
    PRODUCER("producer"),
    CONSUMER("consumer"),
    CONNECT("connect"),
    STREAM("stream");

    private final String name;

    KafkaClient(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
