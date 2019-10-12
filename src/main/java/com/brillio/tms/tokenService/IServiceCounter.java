package com.brillio.tms.tokenService;

import com.brillio.tms.enums.TokenCategory;

public interface IServiceCounter {
    String getName();
    String getQueueName();

    TokenCategory servingTokenCategory();
}
