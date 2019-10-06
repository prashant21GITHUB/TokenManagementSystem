package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.AssignedToken;
import com.brillio.tms.tokenGeneration.Token;
import com.brillio.tms.tokenGeneration.TokenCategory;

import java.util.HashMap;
import java.util.Map;

public class ServiceCounterRegistry implements IServiceCounterRegistry {

    private final int NORMAL_CATEGORY_COUNTERS = 5;
    private final int PREMIUM_CATEGORY_COUNTERS = 3;
    private int nextCounterNum = 1;

    private final Map<Integer, IServiceCounter> normalCategoryCounters;
    private final Map<Integer, IServiceCounter> premiumCategoryCounters;

    public ServiceCounterRegistry() {
        premiumCategoryCounters = createNormalCategoryCounters();
        normalCategoryCounters = createPremiumCategoryCounters();
//        startCounters(premiumCategoryCounters);
//        startCounters(normalCategoryCounters);
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

    private Map<Integer, IServiceCounter> createNormalCategoryCounters() {
        Map<Integer, IServiceCounter> serviceCounterMap = new HashMap<>();
        int num = 0;
        for(;num < NORMAL_CATEGORY_COUNTERS;) {
            ServiceCounter serviceCounter = new ServiceCounter(nextCounterNum++, TokenCategory.NORMAL);
            serviceCounterMap.put(num++, serviceCounter);
        }
        return serviceCounterMap;
    }

    private Map<Integer, IServiceCounter> createPremiumCategoryCounters() {
        Map<Integer, IServiceCounter> serviceCounterMap = new HashMap<>();
        int num = 0;
        for(;num < PREMIUM_CATEGORY_COUNTERS;) {
            ServiceCounter serviceCounter = new ServiceCounter(nextCounterNum++, TokenCategory.PREMIUM);
            serviceCounterMap.put(num++, serviceCounter);
        }
        return serviceCounterMap;
    }

//    private void startCounters(Map<Integer, IServiceCounter> countersMap) {
//        for(IServiceCounter counter : countersMap.values()) {
//            counter.startCounter();
//        }
//    }

}
