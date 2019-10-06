package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.AssignedToken;
import com.brillio.tms.tokenGeneration.Token;

public interface ITokenServiceCounterAssignerService {
    AssignedToken assignToken(Token token);
}
