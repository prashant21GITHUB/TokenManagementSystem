package com.brillio.tms.controllers;

import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.kafka.IKafkaServiceMonitor;
import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;
import com.brillio.tms.tokenGeneration.ITokenGenerationService;
import com.brillio.tms.tokenService.IServiceCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("brillio/tms")
public class TMSController {

    private final ITokenGenerationService tokenGenerationService;
    private final IKafkaServiceMonitor kafkaServiceMonitor;
    private final AtomicBoolean isServiceRunning = new AtomicBoolean(false);
    private final AtomicLong reqId = new AtomicLong(1);
    private final Logger LOGGER = LoggerFactory.getLogger("TMSController");

    @Autowired
    public TMSController(ITokenGenerationService tokenGenerationService, IKafkaServiceMonitor kafkaServiceMonitor) {
        this.tokenGenerationService = tokenGenerationService;
        this.kafkaServiceMonitor = kafkaServiceMonitor;
        this.kafkaServiceMonitor.startMonitoring((runningStatus) -> isServiceRunning.set(runningStatus));
    }

    @PostMapping(value = "/generateToken")
    @ResponseBody
    public GenerateTokenResponse generateToken(@RequestBody GenerateTokenRequest request) {
        GenerateTokenResponse response = new GenerateTokenResponse(request.getApplicant());
        long requestId = reqId.getAndIncrement();
        LOGGER.info("ReqId: " + requestId+ ", Request: "+ request);
        boolean success = false;
        if(isServiceRunning.get()) {
            try {
                if(request.getTokenCategory() == null) {
                    response.setErrorMessage("Invalid tokenCategoty, input category as NORMAL or PREMIUM");
                    LOGGER.error("ReqId: " + requestId+ ", Response: "+ response);
                } else {
                    Optional<AssignedToken> assignedTokenOptional = getAssignedToken(request, requestId);
                    if (assignedTokenOptional.isPresent()) {
                        AssignedToken assignedToken = assignedTokenOptional.get();
                        Token token = assignedToken.getToken();
                        IServiceCounter serviceCounter = assignedToken.getServiceCounter();
                        response.setToken(token);
                        response.setServiceCounter(serviceCounter.getName());
                        success = true;
                    } else {
                        response.setErrorMessage("Failed to generate token");
                    }
                }
            } catch (DocumentVerificationException e) {
                response.setErrorMessage(e.getMessage());
            }
        } else {
            response.setErrorMessage("Kafka server not running");
        }
        if(success) {
            LOGGER.info("ReqId: " + requestId+ ", Response: "+ response);
        } else {
            LOGGER.error("ReqId: " + requestId+ ", Error: "+ response.getErrorMessage());
        }
        return response;
    }

    private Optional<AssignedToken> getAssignedToken(GenerateTokenRequest request, long requestId) throws DocumentVerificationException {
        TokenCategory tokenCategory = TokenCategory.NORMAL;
        if(request.getTokenCategory().equals(TokenCategory.PREMIUM)) {
            tokenCategory = TokenCategory.PREMIUM;
        }
        return tokenGenerationService.generateTokenAndAssignServiceCounter(request.getApplicant(),
                request.getDocument(), tokenCategory, requestId);
    }
}
