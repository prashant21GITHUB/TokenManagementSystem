package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.TokenCategory;

public interface IServiceCounter {
    String getName();
    String getQueueName();

    TokenCategory servingTokenCategory();
}
