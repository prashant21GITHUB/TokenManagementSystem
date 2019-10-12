package com.brillio.tms.kafka.json;

import com.brillio.tms.models.ApplicantTokenRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class ObjectDeserializer implements Deserializer<ApplicantTokenRecord> {
    private ObjectMapper mapper = new ObjectMapper();

    public ObjectDeserializer() {
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
