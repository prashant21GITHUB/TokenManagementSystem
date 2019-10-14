package com.brillio.tms.tokenService;

import com.brillio.tms.enums.TokenCategory;

/**
 *  Each service counter must subscribe to a kafka topic(Queue name).
 *  Implementation: {@link ServiceCounter}
 *
 */
public interface IServiceCounter {
    String getName();
    String getQueueName();

    TokenCategory servingTokenCategory();
}
