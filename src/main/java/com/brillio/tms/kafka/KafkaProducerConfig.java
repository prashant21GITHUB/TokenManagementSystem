package com.brillio.tms.kafka;

import com.brillio.tms.models.ApplicantTokenRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${bootstrap.servers}")
    private String bootstrapServers;
    @Value(value = "${key.serializer}")
    private String keySerializer;
    @Value(value = "${value.serializer}")
    private String valueSerializer;

    @Bean
    public ProducerFactory<String, ApplicantTokenRecord> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                keySerializer);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "com.brillio.tms.kafka.json.ObjectSerializer");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ApplicantTokenRecord> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
