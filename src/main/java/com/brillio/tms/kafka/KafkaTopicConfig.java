package com.brillio.tms.kafka;

import kafka.admin.RackAwareMode;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.Time;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import scala.collection.Seq;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC = "brillio-tms";

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;
    private AdminZkClient adminZkClient;
    private KafkaZkClient zkClient;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

//    @Bean
//    public NewTopic topic1() {
//
//        return new NewTopic(TOPIC, 1,(short)1);
//    }


    public void init() {
        try{
            String zookeeperHost = bootstrapAddress;
            Boolean isSucre = false;
            int sessionTimeoutMs = 200000;
            int connectionTimeoutMs = 15000;
            int maxInFlightRequests = 10;
            Time time = Time.SYSTEM;
            String metricGroup = "myGroup";
            String metricType = "myType";
            zkClient = KafkaZkClient.apply(zookeeperHost,isSucre,sessionTimeoutMs,
                    connectionTimeoutMs,maxInFlightRequests,time,metricGroup,metricType);
            this.adminZkClient = new AdminZkClient(zkClient);

            String topicName1 = "myTopic";
            int partitions = 3;
            int replication = 1; // you should have replication factor less than or equal to number of nodes in Kafka cluster
            Properties topicConfig = new Properties();
            adminZkClient.createTopic(topicName1,partitions,replication,topicConfig, RackAwareMode.Disabled$.MODULE$);
            System.out.println("List topics: " + adminZkClient.getAllTopicConfigs().get(topicName1));

            Seq allTopic = zkClient.getAllTopicsInCluster();
            System.out.println("Cluster has " + allTopic.length() + " topics");
            System.out.println(allTopic);
        } catch (Exception e) {

        }
    }

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

    public void subscribeToTopic(String topicName, ConsumerRecordsListener consumerRecordsListener) {
        // Create the consumer using props.
        final Consumer<Long, String> consumer =  new KafkaConsumer<>(kafkaProperties);
        consumer.subscribe(Collections.singletonList(topicName));
        while (true) {
            consumerRecordsListener.onNewConsumerRecords(consumer.poll(Duration.ofMillis(100)));
        }
    }

    private void loadProperties(Properties properties) {
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "brillio");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonSerializer.class.getName());
    }
//
//    void consumer() {
//        final Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                bootstrapAddress);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG,
//                "KafkaExampleConsumer");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//                LongDeserializer.class.getName());
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//                JsonDeserializer.class.getName());
//
//        // Create the consumer using props.
//        final Consumer<Long, String> consumer =
//                new KafkaConsumer<>(props);
//
//        // Subscribe to the topic.
//        consumer.subscribe(Collections.singletonList(TOPIC));
//        consumer.
//    }
}
