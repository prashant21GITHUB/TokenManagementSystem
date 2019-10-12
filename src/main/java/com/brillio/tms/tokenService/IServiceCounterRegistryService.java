package com.brillio.tms.tokenService;

import com.brillio.tms.models.Token;

public interface IServiceCounterRegistryService {
    IServiceCounter getServiceCounterForToken(Token token);
}
