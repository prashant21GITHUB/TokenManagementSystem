package com.brillio.tms.kafka;

/**
 * Callback listener provided by each subscriber to {@link KafkaMonitorService}
 */
public interface KafkaServiceListener {
    void onRunningStatusChanged(boolean isRunning);
}
