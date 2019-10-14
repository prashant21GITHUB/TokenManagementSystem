package com.brillio.tms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/**
 * See {@literal application.properties} and {@literal kafka.properties} files under resources directory
 *
 */
@Component
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:kafka.properties")
})
public class TMSConfig {

    //Kafka settings
    @Value(value = "${server.ping.interval.millis:5000}")
    private long kafkaServerPingInterval;
    @Value(value = "${bootstrap.servers:localhost:9092}")
    private String bootstrapServers;
    @Value(value = "${group.id:brillio}")
    private String groupId;
    @Value(value = "${max.poll.records:100}")
    private int maxPollRecords;
    @Value(value = "${enable.auto.commit:false}")
    private boolean enableAutoCommitFlag;
    @Value(value = "${auto.commit.interval.ms:1000}")
    private long autoCommitIntervalMillis;
    @Value(value = "${session.timeout.ms:30000}")
    private long sessionTimeoutMillis;
    @Value(value = "${heartbeat.interval.ms:10000}")
    private long heartbeatIntervalMillis;

    //Application settings
    @Value(value = "${service.counter.id.category.pairs}")
    private String[] serviceCounterList;
    @Value(value = "${service.counter.queue.names}")
    private String[] serviceCounterQueueNames;
    @Value(value = "${token.generation.counters.size}")
    private int tokenGenerationCountersSize;

    public long getKafkaServerPingInterval() {
        return kafkaServerPingInterval;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public boolean getEnableAutoCommitFlag() {
        return enableAutoCommitFlag;
    }

    public long getAutoCommitIntervalMillis() {
        return autoCommitIntervalMillis;
    }

    public long getSessionTimeoutMillis() {
        return sessionTimeoutMillis;
    }

    public int getMaxPollRecords() {
        return maxPollRecords;
    }

    public String[] getServiceCounterList() {
        return serviceCounterList;
    }

    public String[] getServiceCounterQueueNames() {
        return serviceCounterQueueNames;
    }

    public int getTokenGenerationCountersSize() {
        return tokenGenerationCountersSize;
    }

    public long getHeartbeatIntervalMillis() {
        return heartbeatIntervalMillis;
    }
}
