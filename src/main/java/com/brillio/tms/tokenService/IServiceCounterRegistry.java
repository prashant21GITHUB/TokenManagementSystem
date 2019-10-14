package com.brillio.tms.tokenService;

import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;

public interface IServiceCounterRegistry {
    AssignedToken assignServiceCounter(Token token);
}
