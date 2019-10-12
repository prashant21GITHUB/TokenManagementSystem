package com.brillio.tms.kafka;

public interface KafkaServiceListener {
    void onRunningStatusChanged(boolean isRunning);
}
