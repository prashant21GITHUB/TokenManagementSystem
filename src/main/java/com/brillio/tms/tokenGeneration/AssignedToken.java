package com.brillio.tms.tokenGeneration;

import com.brillio.tms.tokenService.IServiceCounter;

public class AssignedToken {

    private final Token token;
    private final IServiceCounter serviceCounter;

    public AssignedToken(Token token, IServiceCounter serviceCounter) {
        this.token = token;
        this.serviceCounter = serviceCounter;
    }

    public Token getToken() {
        return token;
    }

    public IServiceCounter getServiceCounter() {
        return serviceCounter;
    }

    public static final AssignedToken EMPTY_TOKEN = new AssignedToken(null, null);
}
