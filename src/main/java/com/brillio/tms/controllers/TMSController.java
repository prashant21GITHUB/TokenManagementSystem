package com.brillio.tms.controllers;

import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.kafka.IKafkaServiceMonitor;
import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;
import com.brillio.tms.tokenGeneration.ITokenGenerationService;
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
        this.kafkaServiceMonitor.startMonitoring((runningStatus) -> isServiceRunning.set(runningStatus));
    }

    @PostMapping(value = "/generateToken")
    @ResponseBody
    public GenerateTokenResponse generateToken(@RequestBody GenerateTokenRequest request) {
        GenerateTokenResponse response = new GenerateTokenResponse(request.getApplicant());
        if(isServiceRunning.get()) {
            try {
                if(request.getTokenCategory() == null) {
                    response.setErrorMessage("Invalid tokenCategoty, input category as NORMAL or PREMIUM");
                } else {
                    Optional<AssignedToken> assignedTokenOptional = getAssignedToken(request);
                    if (assignedTokenOptional.isPresent()) {
                        AssignedToken assignedToken = assignedTokenOptional.get();
                        Token token = assignedToken.getToken();
                        IServiceCounter serviceCounter = assignedToken.getServiceCounter();
                        response.setToken(token);
                        response.setServiceCounter(serviceCounter.getName());
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
        return response;
    }

    private Optional<AssignedToken> getAssignedToken(GenerateTokenRequest request) throws DocumentVerificationException {
        TokenCategory tokenCategory = TokenCategory.NORMAL;
        if(request.getTokenCategory().equals(TokenCategory.PREMIUM)) {
            tokenCategory = TokenCategory.PREMIUM;
        }
        return tokenGenerationService.generateToken(request.getApplicant(),
                request.getDocument(), tokenCategory);
    }

//    @PostMapping(value = "/generatePremiumToken")
//    @ResponseBody
//    public GenerateTokenResponse generatePremiumToken(@RequestBody GenerateTokenRequest request) {
//        GenerateTokenResponse response = new GenerateTokenResponse(request.getApplicant());
//        if(isServiceRunning.get()) {
//            try {
//                Optional<AssignedToken> assignedTokenOptional = tokenGenerationService.generatePremiumToken(request.getApplicant(),
//                        request.getDocument());
//                if (assignedTokenOptional.isPresent()) {
//                    AssignedToken assignedToken = assignedTokenOptional.get();
//                    Token token = assignedToken.getToken();
//                    IServiceCounter serviceCounter = assignedToken.getServiceCounter();
//                    response.setToken(token);
//                    response.setServiceCounter(serviceCounter.getName());
//                } else {
//                    response.setErrorMessage("Failed to generate token");
//                }
//            } catch (DocumentVerificationException e) {
//                response.setErrorMessage(e.getMessage());
//            }
//        } else {
//            response.setErrorMessage("Kafka server not running");
//        }
//        return response;
//    }
}
