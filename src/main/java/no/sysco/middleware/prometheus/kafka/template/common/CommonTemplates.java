package no.sysco.middleware.prometheus.kafka.template.common;

import org.apache.kafka.common.MetricNameTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * CommonTemplates class has templates for common metrics of kafka clients
 * https://kafka.apache.org/documentation/#selector_monitoring
 * kafka.[producer|consumer|connect]:type=[producer|consumer|connect]-metrics,client-id=([-.\w]+)
 * */
public class CommonTemplates {

    public final String metricGroupName;
    public final Set<MetricNameTemplate> templates;

    private final MetricNameTemplate connectionCloseRate;
    private final MetricNameTemplate connectionCloseTotal;
    private final MetricNameTemplate connectionCreationRate;
    private final MetricNameTemplate connectionCreationTotal;
    private final MetricNameTemplate networkIoRate;
    private final MetricNameTemplate networkIoTotal;
    private final MetricNameTemplate outgoingByteRate;
    private final MetricNameTemplate outgoingByteTotal;
    private final MetricNameTemplate requestRate;
    private final MetricNameTemplate requestTotal;
    private final MetricNameTemplate requestSizeAvg;
    private final MetricNameTemplate requestSizeMax;
    private final MetricNameTemplate incomingByteRate;
    private final MetricNameTemplate incomingByteTotal;
    private final MetricNameTemplate responseRate;
    private final MetricNameTemplate responseTotal;
    private final MetricNameTemplate selectRate;
    private final MetricNameTemplate selectTotal;
    private final MetricNameTemplate ioWaitTimeNsAvg;
    private final MetricNameTemplate ioWaitRatio;
    private final MetricNameTemplate ioTimeNsAvg;
    private final MetricNameTemplate ioRatio;
    private final MetricNameTemplate connectionCount;

    // exist when client start communicate with kafka cluster
    private final MetricNameTemplate successfulAuthenticationRate;
    private final MetricNameTemplate successfulAuthenticationTotal;
    private final MetricNameTemplate failedAuthenticationRate;
    private final MetricNameTemplate failedAuthenticationTotal;
    private final MetricNameTemplate successfulReauthenticationRate;
    private final MetricNameTemplate successfulReauthenticationTotal;
    private final MetricNameTemplate reauthenticationLatencyMax;
    private final MetricNameTemplate reauthenticationLatencyAvg;
    private final MetricNameTemplate failedReauthenticationRate;
    private final MetricNameTemplate failedReauthenticationTotal;
    private final MetricNameTemplate successfulAuthenticationNoReauthTotal;

    public CommonTemplates(KafkaClient kafkaClient) {
        this.metricGroupName = kafkaClient + "-metrics";
        this.templates = new HashSet<>();
        Set<String> tags = new HashSet<>(Collections.singletonList("client-id"));

        this.connectionCloseRate = createTemplate("connection-close-rate", "Connections closed per second in the window.", tags);
        this.connectionCloseTotal = createTemplate("connection-close-total", "Total connections closed in the window.", tags);
        this.connectionCreationRate = createTemplate("connection-creation-rate", "New connections established per second in the window.", tags);
        this.connectionCreationTotal = createTemplate("connection-creation-total", "Total new connections established in the window.", tags);
        this.networkIoRate = createTemplate("network-io-rate", "The average number of network operations (reads or writes) on all connections per second.", tags);
        this.networkIoTotal = createTemplate("network-io-total", "The total number of network operations (reads or writes) on all connections.", tags);
        this.outgoingByteRate = createTemplate("outgoing-byte-rate", "The average number of outgoing bytes sent per second to all servers.", tags);
        this.outgoingByteTotal = createTemplate("outgoing-byte-total", "The total number of outgoing bytes sent to all servers.", tags);
        this.requestRate = createTemplate("request-rate", "The average number of requests sent per second.", tags);
        this.requestTotal = createTemplate("request-total", "The total number of requests sent.", tags);
        this.requestSizeAvg = createTemplate("request-size-avg", "The average size of all requests in the window.", tags);
        this.requestSizeMax = createTemplate("request-size-max", "The maximum size of any request sent in the window.", tags);
        this.incomingByteRate = createTemplate("incoming-byte-rate", "Bytes/second read off all sockets.", tags);
        this.incomingByteTotal = createTemplate("incoming-byte-total", "Total bytes read off all sockets.", tags);
        this.responseRate = createTemplate("response-rate", "Responses received per second.", tags);
        this.responseTotal = createTemplate("response-total", "Total responses received.", tags);
        this.selectRate = createTemplate("select-rate", "Number of times the I/O layer checked for new I/O to perform per second.", tags);
        this.selectTotal = createTemplate("select-total", "Total number of times the I/O layer checked for new I/O to perform.", tags);
        this.ioWaitTimeNsAvg = createTemplate("io-wait-time-ns-avg", "The average length of time the I/O thread spent waiting for a socket ready for reads or writes in nanoseconds.", tags);
        this.ioWaitRatio = createTemplate("io-wait-ratio", "The fraction of time the I/O thread spent waiting.", tags);
        this.ioTimeNsAvg = createTemplate("io-time-ns-avg", "The average length of time for I/O per select call in nanoseconds.", tags);
        this.ioRatio = createTemplate("io-ratio", "The fraction of time the I/O thread spent doing I/O.", tags);
        this.connectionCount = createTemplate("connection-count", "The current number of active connections.", tags);

        this.successfulAuthenticationRate = createTemplate("successful-authentication-rate", "Connections per second that were successfully authenticated using SASL or SSL.", tags);
        this.successfulAuthenticationTotal = createTemplate("successful-authentication-total", "Total connections that were successfully authenticated using SASL or SSL.", tags);
        this.failedAuthenticationRate = createTemplate("failed-authentication-rate", "Connections per second that failed authentication.", tags);
        this.failedAuthenticationTotal = createTemplate("failed-authentication-total", "Total connections that failed authentication.", tags);
        this.successfulReauthenticationRate = createTemplate("successful-reauthentication-rate", "Connections per second that were successfully re-authenticated using SASL.", tags);
        this.successfulReauthenticationTotal = createTemplate("successful-reauthentication-total", "Total connections that were successfully re-authenticated using SASL.", tags);
        this.reauthenticationLatencyMax = createTemplate("reauthentication-latency-max", "The maximum latency in ms observed due to re-authentication.", tags);
        this.reauthenticationLatencyAvg = createTemplate("reauthentication-latency-avg", "The average latency in ms observed due to re-authentication.", tags);
        this.failedReauthenticationRate = createTemplate("failed-reauthentication-rate", "Connections per second that failed re-authentication.", tags);
        this.failedReauthenticationTotal = createTemplate("failed-reauthentication-total", "Total connections that failed re-authentication.", tags);
        this.successfulAuthenticationNoReauthTotal = createTemplate("successful-authentication-no-reauth-total", "Total connections that were successfully authenticated by older, pre-2.2.0 SASL clients that do not support re-authentication. May only be non-zero", tags);
    }

    private MetricNameTemplate createTemplate(String name, String description, Set<String> tags) {
        MetricNameTemplate metricNameTemplate = new MetricNameTemplate(name, metricGroupName, description, tags);
        templates.add(metricNameTemplate);
        return metricNameTemplate;
    }

}
