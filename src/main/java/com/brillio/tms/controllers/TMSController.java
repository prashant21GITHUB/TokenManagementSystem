package com.brillio.tms.controllers;

import com.brillio.tms.kafka.IKafkaServiceMonitor;
import com.brillio.tms.kafka.KafkaServiceListener;
import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;
import com.brillio.tms.tokenGeneration.*;
import com.brillio.tms.tokenService.IServiceCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("brillio/tms")
public class TMSController {

    private final ITokenGenerationService tokenGenerationService;
    private final IKafkaServiceMonitor kafkaServiceMonitor;
    private final AtomicBoolean isServiceRunning = new AtomicBoolean(false);

    @Autowired
    public TMSController(ITokenGenerationService tokenGenerationService, IKafkaServiceMonitor kafkaServiceMonitor) {
        this.tokenGenerationService = tokenGenerationService;
        this.kafkaServiceMonitor = kafkaServiceMonitor;
        this.kafkaServiceMonitor.startMonitoring(new KafkaServiceListener() {
            @Override
            public void onRunningStatusChanged(boolean isRunning) {
                isServiceRunning.set(isRunning);
            }
        });
    }

    @PostMapping(value = "/generateToken")
    @ResponseBody
    public GenerateTokenResponse generateToken(@RequestBody GenerateTokenRequest request) {
        GenerateTokenResponse response = new GenerateTokenResponse(request.getApplicant());
        if(isServiceRunning.get()) {
            Optional<AssignedToken> assignedTokenOptional = tokenGenerationService.generateToken(request.getApplicant(),
                    request.getDocument());
            if (assignedTokenOptional.isPresent()) {
                AssignedToken assignedToken = assignedTokenOptional.get();
                Token token = assignedToken.getToken();
                IServiceCounter serviceCounter = assignedToken.getServiceCounter();
                String msg = "Token generated : " + token.getTokenNumber() +
                        " Assigned to counter no: " + serviceCounter.getName();
                response.setToken(token);
                response.setServiceCounter(serviceCounter.getName());
            }
        } else {
            response.setErrorMessage("Kafka server not running");
        }
        return response;
    }

    @PostMapping(value = "/generatePremiumToken")
    @ResponseBody
    public GenerateTokenResponse generatePremiumToken(@RequestBody GenerateTokenRequest request) {
        GenerateTokenResponse response = new GenerateTokenResponse(request.getApplicant());
        if(isServiceRunning.get()) {
            Optional<AssignedToken> assignedToken = tokenGenerationService.generatePremiumToken(request.getApplicant(),
                    request.getDocument());
            if (assignedToken.isPresent()) {
                Token token = assignedToken.get().getToken();
                IServiceCounter serviceCounter = assignedToken.get().getServiceCounter();
                response.setToken(token);
                response.setServiceCounter(serviceCounter.getName());
            }
        } else {
            response.setErrorMessage("Kafka server not running");
        }
        return response;
    }
}
