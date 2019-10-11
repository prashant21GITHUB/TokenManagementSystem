package com.brillio.tms.tokenService;

import com.brillio.tms.kafka.ApplicantTokenRecord;
import com.brillio.tms.kafka.KafkaConsumerConfig;
import com.brillio.tms.tokenGeneration.Applicant;
import com.brillio.tms.tokenGeneration.Token;
import com.brillio.tms.tokenGeneration.TokenCategory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceCounter implements IServiceCounter {

    private final BlockingQueue<Token> tokensQueue;
    private final static int MAX_REQUESTS = 50;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final TokenCategory category;
    private ExecutorService executorService;
    private final String queueName;
    private final String counterName;
    private final Consumer<String, ApplicantTokenRecord> kafkaConsumer;
    private final AtomicBoolean isSubscribedToTopic = new AtomicBoolean(false);

    public ServiceCounter(TokenCategory category,
                          String counterName,
                          String queueName,
                          KafkaConsumerConfig kafkaConsumerConfig) {
        this.queueName = queueName;
        this.counterName = counterName;
        this.tokensQueue = new LinkedBlockingQueue<>(MAX_REQUESTS);
        this.category = category;
        kafkaConsumer = kafkaConsumerConfig.newConsumer();
    }

    private void serveToken(Token token, Applicant applicant) {
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
        kafkaConsumer.subscribe(Collections.singletonList(queueName));
        isSubscribedToTopic.set(true);
        executorService.submit(() -> {
            while (isSubscribedToTopic.get()) {
                ConsumerRecords<String, ApplicantTokenRecord> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, ApplicantTokenRecord> record : consumerRecords) {
                    ApplicantTokenRecord tokenRecord = record.value();
                    serveToken(tokenRecord.getToken(), tokenRecord.getApplicant());
                }
            }
        });
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
