package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.Token;

public interface IServiceCounter {
    void serveToken(Token token);
    int getServiceCounterNo();
}
