package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.AssignedToken;
import com.brillio.tms.tokenGeneration.Token;

import javax.inject.Inject;

public class TokenServiceCounterAssignerService implements ITokenServiceCounterAssignerService {

    private final ServiceCounterRegistry serviceCounterRegistry;

    @Inject
    public TokenServiceCounterAssignerService(ServiceCounterRegistry serviceCounterRegistry) {
        this.serviceCounterRegistry = serviceCounterRegistry;
    }

    @Override
    public AssignedToken assignToken(Token token) {
        return serviceCounterRegistry.assignServiceCounter(token);
    }
}
