package com.brillio.tms.tokenService;

import com.brillio.tms.models.Token;

/**
 * Creates and maintains the list of {@link ServiceCounter}
 * Implementation: {@link ServiceCounterRegistry}
 */
public interface IServiceCounterRegistryService {
    IServiceCounter getServiceCounterForToken(Token token);
}
