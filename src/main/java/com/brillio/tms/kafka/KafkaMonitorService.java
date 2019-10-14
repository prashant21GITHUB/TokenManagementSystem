package com.brillio.tms.kafka;

import com.brillio.tms.IAppService;
import com.brillio.tms.TMSConfig;
import com.brillio.tms.annotation.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

@AppService
@Service
public class KafkaMonitorService implements IAppService, IKafkaServiceMonitor {

    private final long kafkaServerPingInterval;
    private final KafkaTopicService kafkaTopicService;
    private final List<KafkaServiceListener> listenerList;
    private ExecutorService executorService;
    private final AtomicBoolean keepMonitoring = new AtomicBoolean(false);
    private static final Logger LOGGER= LoggerFactory.getLogger("KafkaMonitorService");

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
    public void start() {
        executorService = Executors.newFixedThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setName("KMS_thread");
            return t;
        });
        LOGGER.info("Start monitoring kafka server running status");
        executorService.submit(() -> {
            keepMonitoring.set(true);
            boolean status;
            try {
                while (keepMonitoring.get()) {
                    status = kafkaTopicService.isKafkaServerRunning();
                    if(status) {
                        LOGGER.info("Kafka server available");
                    } else {
                        LOGGER.info("Kafka server not available");
                    }
                    for (KafkaServiceListener listener : listenerList) {
                        listener.onRunningStatusChanged(status);
                    }
                    Thread.sleep(kafkaServerPingInterval);
                }
            } catch (Exception e)  {

            }
        });
    }

    @Override
    public void stop() {
        keepMonitoring.set(false);
        executorService.shutdown();
    }
}
