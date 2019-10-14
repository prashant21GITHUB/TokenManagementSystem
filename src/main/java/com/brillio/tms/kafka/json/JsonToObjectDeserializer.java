package com.brillio.tms.kafka.json;

import com.brillio.tms.kafka.KafkaConsumerService;
import com.brillio.tms.models.ApplicantTokenRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 *  Used by {@link KafkaConsumerService#loadProperties()}
 */
@Component
public class JsonToObjectDeserializer implements Deserializer<ApplicantTokenRecord> {
    private ObjectMapper mapper = new ObjectMapper();

    public JsonToObjectDeserializer() {
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public ApplicantTokenRecord deserialize(String topic, byte[] data) {
        try {
            return mapper.readValue(data, ApplicantTokenRecord.class);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() {

    }
}
