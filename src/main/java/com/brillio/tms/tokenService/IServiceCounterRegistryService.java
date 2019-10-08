package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.AssignedToken;
import com.brillio.tms.tokenGeneration.Token;

public interface IServiceCounterRegistryService {
    AssignedToken assignServiceCounter(Token token);
}
