package com.brillio.tms;

/**
 * Its implementations are called by sprint boot application start up to call their start() methods to start
 * the corresponding thread pools. See {@link TmsApplication}
 *
 * Following are the implementations:
 *  {@link com.brillio.tms.kafka.KafkaMonitorService},
 *  {@link com.brillio.tms.tokenGeneration.TokenGenerationServiceImpl}, and
 *  {@link com.brillio.tms.tokenService.ServiceCounterRegistry}
 *
 */
public interface IAppService {
    void start();
    void stop();
}
