package com.brillio.tms.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@PropertySource("classpath:kafka.properties")
public class KafkaTopicConfig {

    private final Properties kafkaProperties = new Properties();

    @PostConstruct
    public void loadKafkaProperties() {
        loadProperties(kafkaProperties);
    }

    public void createTopic(String topicName) {
        AdminClient adminClient = null;
        try {
            adminClient = AdminClient.create(kafkaProperties);
            NewTopic newTopic = new NewTopic(topicName, 1, (short)1); //new NewTopic(topicName, numPartitions, replicationFactor)
            List<NewTopic> newTopics = new ArrayList<NewTopic>();
            newTopics.add(newTopic);
            adminClient.createTopics(newTopics);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(adminClient != null) {
                adminClient.close();
            }
        }
    }

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
