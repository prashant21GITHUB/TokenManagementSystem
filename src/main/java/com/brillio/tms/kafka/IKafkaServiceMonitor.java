package com.brillio.tms.kafka;

public interface IKafkaServiceMonitor {
    void startMonitoring(KafkaServiceListener listener);
    void stopMonitoring(KafkaServiceListener listener);
}
