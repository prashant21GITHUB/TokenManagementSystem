package com.brillio.tms.tokenService;

import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.kafka.KafkaConsumerConfig;
import com.brillio.tms.kafka.KafkaMonitorService;
import com.brillio.tms.kafka.KafkaServiceListener;
import com.brillio.tms.models.ApplicantTokenRecord;
import com.brillio.tms.models.Token;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceCounter implements IServiceCounter {

    private final BlockingQueue<Token> tokensQueue;
    private final static int MAX_REQUESTS = 500;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final TokenCategory category;
    private ExecutorService executorService;
    private final String queueName;
    private final String counterName;
    private final Consumer<String, ApplicantTokenRecord> kafkaConsumer;
    private final AtomicBoolean isSubscribedToTopic = new AtomicBoolean(false);
    private final KafkaMonitorService kafkaMonitorService;
    private final AtomicBoolean isKafkaServiceRunning = new AtomicBoolean(false);
    private ExecutorService waitingThread;

    public ServiceCounter(TokenCategory category,
                          String counterName,
                          String queueName,
                          KafkaConsumerConfig kafkaConsumerConfig, KafkaMonitorService kafkaMonitorService) {
        this.queueName = queueName;
        this.counterName = counterName;
        this.kafkaMonitorService = kafkaMonitorService;
        this.tokensQueue = new LinkedBlockingQueue<>(MAX_REQUESTS);
        this.category = category;
        this.kafkaConsumer = kafkaConsumerConfig.newConsumer();
        this.kafkaMonitorService.startMonitoring(new KafkaServiceListener() {
            @Override
            public void onRunningStatusChanged(boolean isRunning) {
                isKafkaServiceRunning.set(isRunning);
            }
        });
        this.waitingThread = Executors.newFixedThreadPool(1);
    }

    private void serveToken(Token token) {
        try {
            tokensQueue.put(token);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startCounter() {
        if(!isStarted.getAndSet(true)) {
            executorService = Executors.newFixedThreadPool(2, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("ServiceCounter_" + ServiceCounter.this.counterName);
                    return t;
                }
            });

            executorService.submit(() -> {
               while (isStarted.get()) {
                   try {
                       Token token = tokensQueue.take();
                       if(Token.EMPTY_TOKEN.equals(token)) {
                           tokensQueue.clear();
                           break;
                       }
                       System.out.println("Serving applicant with token no. " +
                               token.getTokenNumber() + " at counter. " + counterName);
                   } catch (InterruptedException e) {
                       //TODO:
                       e.printStackTrace();
                   }
               }
            });

            subscribeToTopic();
            return;
        }
    }

    private void subscribeToTopic() {
        kafkaConsumer.subscribe(Collections.singletonList(queueName), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println("Assigned: "+ partitions);
            }
        });

        isSubscribedToTopic.set(true);
        executorService.submit(() -> {
            while (isSubscribedToTopic.get()) {
                waitIfKafkaServiceIsNotRunning();
                try {
                    ConsumerRecords<String, ApplicantTokenRecord> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                    if(!consumerRecords.isEmpty()) {
                        for (ConsumerRecord<String, ApplicantTokenRecord> record : consumerRecords) {
                            ApplicantTokenRecord tokenRecord = record.value();
                            serveToken(tokenRecord.getToken());
                        }
                        kafkaConsumer.commitSync();
                    }

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    private void waitIfKafkaServiceIsNotRunning() {
        if(!isKafkaServiceRunning.get()) {
            waitingThread.submit(() -> {
               while (!isKafkaServiceRunning.get()) {
                   try {
                       Thread.sleep(5000 );
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
            });
        }
    }

    public void stopCounter() {
        if(isStarted.getAndSet(false)) {
            try {
                tokensQueue.put(Token.EMPTY_TOKEN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executorService.shutdown();
            kafkaConsumer.unsubscribe();
            isSubscribedToTopic.set(false);
        }
    }

    @Override
    public String getQueueName() {
        return queueName;
    }

    @Override
    public String getName() {
        return counterName;
    }

    @Override
    public TokenCategory servingTokenCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceCounter that = (ServiceCounter) o;

        if (queueName != null ? !queueName.equals(that.queueName) : that.queueName != null) return false;
        return counterName != null ? counterName.equals(that.counterName) : that.counterName == null;
    }

    @Override
    public int hashCode() {
        int result = queueName != null ? queueName.hashCode() : 0;
        result = 31 * result + (counterName != null ? counterName.hashCode() : 0);
        return result;
    }
}
