package com.brillio.tms.tokenService;

import com.brillio.tms.annotation.AppService;
import com.brillio.tms.kafka.KafkaTopicConfig;
import com.brillio.tms.tokenGeneration.AssignedToken;
import com.brillio.tms.tokenGeneration.IAppService;
import com.brillio.tms.tokenGeneration.Token;
import com.brillio.tms.tokenGeneration.TokenCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@AppService
@Repository
public class ServiceCounterRegistry implements IServiceCounterRegistryService, IAppService {

    private final int NORMAL_CATEGORY_COUNTERS = 5;
    private final int PREMIUM_CATEGORY_COUNTERS = 3;
    private final KafkaAdmin kafkaAdmin;
    private int nextCounterNum = 1;

    private final String[] serviceCounterList;
    private final String[] serviceCounterKafkaTopics;

    private Map<Integer, ServiceCounter> normalCategoryCounters;
    private Map<Integer, ServiceCounter> premiumCategoryCounters;
    private final KafkaTopicConfig kafkaTopicConfig;

    @Autowired
    public ServiceCounterRegistry(KafkaAdmin kafkaAdmin,
                                  KafkaTopicConfig topicConfig,
                                  @Value("${service.counter.list}") String[] serviceCounterList,
                                  @Value("${kafka.consumer.counter.topics}") String[] serviceCounterKafkaTopics) {
        this.kafkaAdmin = kafkaAdmin;
        this.serviceCounterList = serviceCounterList;
        this.serviceCounterKafkaTopics = serviceCounterKafkaTopics;
        if(!checkValidityForCounterList()) {
            throw new IllegalArgumentException("Please provide service counter list and corresponding kafka topics");
        }
        createServiceCounters();
        this.kafkaTopicConfig = topicConfig;
//        this.premiumCategoryCounters = createNormalCategoryCounters();
//        this.normalCategoryCounters = createPremiumCategoryCounters();
    }

    private void createServiceCounters() {
        this.premiumCategoryCounters = new HashMap<>();
        this.normalCategoryCounters = new HashMap<>();
        String arr[];
        int index = 0, normalCounterCount = 0, premiumCounterCount = 0;
        for(String counterCategoryPair : serviceCounterList) {
            arr = counterCategoryPair.split(":");
            TokenCategory category = TokenCategory.parse(arr[1]);
            ServiceCounter serviceCounter = new ServiceCounter(nextCounterNum++, category, arr[0],
                    serviceCounterKafkaTopics[index]);
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

    public AssignedToken assignServiceCounter(Token token) {
        int tokenNumber = token.getTokenNumber();
        TokenCategory tokenCategory = token.getTokenCategory();
        if(TokenCategory.PREMIUM.equals(tokenCategory)) {
            int hash = tokenNumber % premiumCategoryCounters.size();
            return new AssignedToken(token, premiumCategoryCounters.get(hash));
        } else {
            int hash = tokenNumber % normalCategoryCounters.size();
            return new AssignedToken(token, normalCategoryCounters.get(hash));
        }
    }

    private Map<Integer, ServiceCounter> createNormalCategoryCounters() {
        Map<Integer, ServiceCounter> serviceCounterMap = new HashMap<>();
        int num = 0;
        for(;num < NORMAL_CATEGORY_COUNTERS;) {
            ServiceCounter serviceCounter = new ServiceCounter(nextCounterNum++, TokenCategory.NORMAL, "", "");
            serviceCounterMap.put(num++, serviceCounter);
        }
        return serviceCounterMap;
    }

    private Map<Integer, ServiceCounter> createPremiumCategoryCounters() {
        Map<Integer, ServiceCounter> serviceCounterMap = new HashMap<>();
        int num = 0;
        for(;num < PREMIUM_CATEGORY_COUNTERS;) {
            ServiceCounter serviceCounter = new ServiceCounter(nextCounterNum++, TokenCategory.PREMIUM, "", "");
            serviceCounterMap.put(num++, serviceCounter);
        }
        return serviceCounterMap;
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
            kafkaTopicConfig.createTopic(counter.getKafkaTopic());
        }
    }

    private void stopCounters(Map<Integer, ServiceCounter> countersMap) {
        for(ServiceCounter counter : countersMap.values()) {
            counter.stopCounter();
        }
    }

}
