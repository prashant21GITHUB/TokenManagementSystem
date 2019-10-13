package com.brillio.tms.kafka.json;

import com.brillio.tms.models.ApplicantTokenRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ObjectToJsonSerializer implements Serializer<ApplicantTokenRecord> {

    private ObjectMapper mapper = new ObjectMapper();

    public ObjectToJsonSerializer() {
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

    public String convertToJsonString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public void close() {
    }
}
