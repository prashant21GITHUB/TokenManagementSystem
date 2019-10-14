package com.brillio.tms.kafka;

import com.brillio.tms.TMSConfig;
import com.brillio.tms.annotation.AppService;
import com.brillio.tms.tokenGeneration.IAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@AppService
@Service
public class KafkaMonitorService implements IAppService, IKafkaServiceMonitor {

    private final long kafkaServerPingInterval;
    private final KafkaTopicService kafkaTopicService;
    private final List<KafkaServiceListener> listenerList;
    private ExecutorService executorService;
    private final AtomicBoolean keepMonitoring = new AtomicBoolean(false);

    @Autowired
    public KafkaMonitorService(TMSConfig config, KafkaTopicService kafkaTopicService) {
        this.kafkaTopicService = kafkaTopicService;
        this.listenerList = new ArrayList<>();
        this.kafkaServerPingInterval = config.getKafkaServerPingInterval();
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
        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                keepMonitoring.set(true);
                boolean status;
                try {
                    while (keepMonitoring.get()) {
                        status = kafkaTopicService.isKafkaServerRunning();
                        for (KafkaServiceListener listener : listenerList) {
                            listener.onRunningStatusChanged(status);
                        }
                        Thread.sleep(kafkaServerPingInterval);
                    }
                } catch (Exception e)  {

                }
            }
        });
    }

    @Override
    public void stop() {
        keepMonitoring.set(false);
        executorService.shutdown();
    }
}
