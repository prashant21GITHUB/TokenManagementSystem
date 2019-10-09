package com.brillio.tms.tokenService;

import com.brillio.tms.annotation.AppService;
import com.brillio.tms.tokenGeneration.AssignedToken;
import com.brillio.tms.tokenGeneration.IAppService;
import com.brillio.tms.tokenGeneration.Token;
import com.brillio.tms.tokenGeneration.TokenCategory;
import org.apache.kafka.common.internals.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
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

    private final Map<Integer, ServiceCounter> normalCategoryCounters;
    private final Map<Integer, ServiceCounter> premiumCategoryCounters;

    @Autowired
    public ServiceCounterRegistry(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
        premiumCategoryCounters = createNormalCategoryCounters();
        normalCategoryCounters = createPremiumCategoryCounters();
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
            ServiceCounter serviceCounter = new ServiceCounter(nextCounterNum++, TokenCategory.NORMAL);
            serviceCounterMap.put(num++, serviceCounter);
        }
        return serviceCounterMap;
    }

    private Map<Integer, ServiceCounter> createPremiumCategoryCounters() {
        Map<Integer, ServiceCounter> serviceCounterMap = new HashMap<>();
        int num = 0;
        for(;num < PREMIUM_CATEGORY_COUNTERS;) {
            ServiceCounter serviceCounter = new ServiceCounter(nextCounterNum++, TokenCategory.PREMIUM);
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
        }
    }

    private void stopCounters(Map<Integer, ServiceCounter> countersMap) {
        for(ServiceCounter counter : countersMap.values()) {
            counter.stopCounter();
        }
    }

}
