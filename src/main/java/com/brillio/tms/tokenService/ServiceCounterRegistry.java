package com.brillio.tms.tokenService;

import com.brillio.tms.annotation.AppService;
import com.brillio.tms.kafka.KafkaConsumerConfig;
import com.brillio.tms.kafka.KafkaTopicConfig;
import com.brillio.tms.tokenGeneration.IAppService;
import com.brillio.tms.tokenGeneration.Token;
import com.brillio.tms.tokenGeneration.TokenCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@AppService
@Repository
public class ServiceCounterRegistry implements IServiceCounterRegistryService, IAppService {

    private final String[] serviceCounterList;
    private final String[] serviceCounterKafkaTopics;

    private Map<Integer, ServiceCounter> normalCategoryCounters;
    private Map<Integer, ServiceCounter> premiumCategoryCounters;
    private final KafkaTopicConfig kafkaTopicConfig;
    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Autowired
    public ServiceCounterRegistry(KafkaTopicConfig topicConfig,
                                  KafkaConsumerConfig kafkaConsumerConfig,
                                  @Value("${service.counter.id.category.pairs}") String[] serviceCounterList,
                                  @Value("${service.counter.queue.names}") String[] serviceCounterKafkaTopics) {
        this.serviceCounterList = serviceCounterList;
        this.serviceCounterKafkaTopics = serviceCounterKafkaTopics;
        this.kafkaTopicConfig = topicConfig;
        this.kafkaConsumerConfig = kafkaConsumerConfig;
        if(!checkValidityForCounterList()) {
            throw new IllegalArgumentException("Please provide service counter list and corresponding kafka topics");
        }
        createServiceCounters();
    }

    private void createServiceCounters() {
        this.premiumCategoryCounters = new HashMap<>();
        this.normalCategoryCounters = new HashMap<>();
        String arr[];
        int index = 0, normalCounterCount = 0, premiumCounterCount = 0;
        for(String counterCategoryPair : serviceCounterList) {
            arr = counterCategoryPair.split(":");
            TokenCategory category = TokenCategory.parse(arr[1]);
            ServiceCounter serviceCounter = new ServiceCounter(category, arr[0],
                    serviceCounterKafkaTopics[index], kafkaConsumerConfig);
            if(category.equals(TokenCategory.PREMIUM)) {
                premiumCategoryCounters.put(premiumCounterCount++, serviceCounter);
            } else {
                normalCategoryCounters.put(normalCounterCount++, serviceCounter);
            }
            index++;
        }
    }

    private boolean checkValidityForCounterList() {
        if(serviceCounterList == null || serviceCounterList.length == 0 ||
                serviceCounterKafkaTopics == null ||
                serviceCounterKafkaTopics.length != serviceCounterList.length) {
            return false;
        }
        return true;
    }

    public IServiceCounter getServiceCounterForToken(Token token) {
        int tokenNumber = token.getTokenNumber();
        TokenCategory tokenCategory = token.getTokenCategory();
        if(TokenCategory.PREMIUM.equals(tokenCategory)) {
            int hash = tokenNumber % premiumCategoryCounters.size();
            return premiumCategoryCounters.get(hash);
        } else {
            int hash = tokenNumber % normalCategoryCounters.size();
            return normalCategoryCounters.get(hash);
        }
    }

    @Override
    public void start() throws InterruptedException {
        startCounters(premiumCategoryCounters);
        startCounters(normalCategoryCounters);
    }

    @Override
    public void stop() {
        stopCounters(normalCategoryCounters);
        stopCounters(premiumCategoryCounters);
    }

    private void startCounters(Map<Integer, ServiceCounter> countersMap) {
        for(ServiceCounter counter : countersMap.values()) {
            counter.startCounter();
            kafkaTopicConfig.createTopic(counter.getQueueName());
        }
    }

    private void stopCounters(Map<Integer, ServiceCounter> countersMap) {
        for(ServiceCounter counter : countersMap.values()) {
            counter.stopCounter();
        }
    }

}
