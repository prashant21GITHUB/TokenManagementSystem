package com.brillio.tms.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC = "brillio-tms-4";

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }
    List<NewTopic> topics = new ArrayList<>();
    @Bean
    public List<NewTopic> topic1() {
        topics.add(new NewTopic("brillio-tms-22",1,(short)1));
        topics.add(new NewTopic("brillio-tms-23", 1, (short)1));

        return topics;
    }

    @PostConstruct
    public void init() {
        topics.add(new NewTopic("brillio-tms-22",1,(short)1));
        topics.add(new NewTopic("brillio-tms-23", 1, (short)1));
    }
}
