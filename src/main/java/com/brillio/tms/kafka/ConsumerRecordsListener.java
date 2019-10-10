package com.brillio.tms.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface ConsumerRecordsListener<KEY, VALUE> {

    void onNewConsumerRecords(ConsumerRecords<KEY,VALUE> consumerRecords);
}
