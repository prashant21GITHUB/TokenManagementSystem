package com.brillio.tms.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:kafka.properties")
public class KafkaTopicConfig {

    @Value(value = "${bootstrap.servers}")
    private String bootstrapServers;

    private final Properties kafkaProperties = new Properties();

    @PostConstruct
    public void loadProperties() {
        kafkaProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
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

    public boolean isKafkaServerRunning() {
        AdminClient adminClient = null;
        try {
            adminClient = AdminClient.create(kafkaProperties);
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> names = topics.names().get(5000, TimeUnit.MILLISECONDS);
            if (names.isEmpty()) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            if(adminClient != null) {
                adminClient.close();
            }
        }

    }
}
