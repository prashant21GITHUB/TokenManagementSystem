package com.brillio.tms.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import javax.annotation.PostConstruct;
import java.util.Properties;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${bootstrap.servers}")
    private String bootstrapServers;
    @Value(value = "${group.id}")
    private String groupId;
    @Value(value = "${enable.auto.commit}")
    private String enableAutoCommitFlag;
    @Value(value = "${auto.commit.interval.ms}")
    private String autoCommitIntervalMsConfig;
    @Value(value = "${session.timeout.ms}")
    private String sessionTimeoutMsConfig;
    @Value(value = "${key.deserializer}")
    private String keyDeserializer;
    @Value(value = "${value.deserializer}")
    private String valueDeserializer;

    private final Properties kafkaProperties = new Properties();

    @PostConstruct
    public void loadKafkaProperties() {
        loadProperties(kafkaProperties);
    }

    public <K,V> Consumer<K,V> newConsumer() {
        Consumer<K, V> consumer =  new KafkaConsumer<>(kafkaProperties);
        return consumer;
    }

    private void loadProperties(Properties properties) {
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommitFlag);
        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMsConfig);
        properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMsConfig);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                keyDeserializer);
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                valueDeserializer);
    }
}
