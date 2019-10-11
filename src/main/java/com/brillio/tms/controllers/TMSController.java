package com.brillio.tms.controllers;

import com.brillio.tms.tokenGeneration.*;
import com.brillio.tms.tokenService.IServiceCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("brillio/tms")
public class TMSController {

    private final ITokenGenerationService tokenGenerationService;

    @Autowired
    public TMSController(ITokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService;
    }

    @PostMapping(value = "/generateToken")
    @ResponseBody
    public GenerateTokenResponse generateToken(@RequestBody GenerateTokenRequest request) {
        Optional<AssignedToken> assignedTokenOptional = tokenGenerationService.generateToken(request.getApplicant(),
                request.getDocument());
        GenerateTokenResponse response = new GenerateTokenResponse(request.getApplicant());
        if(assignedTokenOptional.isPresent()) {
            AssignedToken assignedToken = assignedTokenOptional.get();
            Token token = assignedToken.getToken();
            IServiceCounter serviceCounter = assignedToken.getServiceCounter();
            String msg = "Token generated : " + token.getTokenNumber() +
                    " Assigned to counter no: "+serviceCounter.getName();
            response.setToken(token);
            response.setServiceCounter(serviceCounter.getName());
        }
        return response;
    }

    @PostMapping(value = "/generatePremiumToken")
    @ResponseBody
    public GenerateTokenResponse generatePremiumToken(@RequestBody GenerateTokenRequest request) {
        Optional<AssignedToken> assignedToken = tokenGenerationService.generatePremiumToken(request.getApplicant(),
                request.getDocument());
        GenerateTokenResponse response = new GenerateTokenResponse(request.getApplicant());
        if(assignedToken.isPresent()) {
            Token token = assignedToken.get().getToken();
            IServiceCounter serviceCounter = assignedToken.get().getServiceCounter();
            response.setToken(token);
            response.setServiceCounter(serviceCounter.getName());
        }
        return response;
    }
}
