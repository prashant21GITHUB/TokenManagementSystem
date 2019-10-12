package com.brillio.tms.kafka.json;

import com.brillio.tms.models.ApplicantTokenRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ObjectSerializer implements Serializer<ApplicantTokenRecord> {

    private ObjectMapper mapper = new ObjectMapper();

    public ObjectSerializer() {
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, ApplicantTokenRecord data) {
        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    }

    @Override
    public void close() {
    }
}
