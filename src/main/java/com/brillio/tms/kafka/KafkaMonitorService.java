package com.brillio.tms.kafka;

import com.brillio.tms.annotation.AppService;
import com.brillio.tms.tokenGeneration.IAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@AppService
@Service
public class KafkaMonitorService implements IAppService, IKafkaServiceMonitor {

    private final KafkaTopicConfig kafkaTopicConfig;
    private final List<KafkaServiceListener> listenerList;
    private ScheduledExecutorService executorService;

    @Autowired
    public KafkaMonitorService(KafkaTopicConfig kafkaTopicConfig) {
        this.kafkaTopicConfig = kafkaTopicConfig;
        listenerList = new ArrayList<>();
    }

    @Override
    public void startMonitoring(KafkaServiceListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void stopMonitoring(KafkaServiceListener listener) {
        listenerList.remove(listener);
    }


    @Override
    public void start() throws InterruptedException {
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                boolean status = kafkaTopicConfig.isKafkaServerRunning();
                for(KafkaServiceListener listener : listenerList) {
                    listener.onRunningStatusChanged(status);
                }
            }
        }, 10000, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        executorService.shutdown();
    }
}
