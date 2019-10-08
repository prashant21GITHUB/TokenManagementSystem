package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.AssignedToken;
import com.brillio.tms.tokenGeneration.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignServiceCounterService implements IAssignServiceCounterService {

    private final ServiceCounterRegistry serviceCounterRegistry;

//    @Inject
    @Autowired
    public AssignServiceCounterService(ServiceCounterRegistry serviceCounterRegistry) {
        this.serviceCounterRegistry = serviceCounterRegistry;
    }

    @Override
    public AssignedToken assignToken(Token token) {
        return serviceCounterRegistry.assignServiceCounter(token);
    }
}
