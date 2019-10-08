package com.brillio.tms.controllers;

import com.brillio.tms.tokenGeneration.*;
import com.brillio.tms.tokenService.IServiceCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("brillio/tms")
public class TMSController {

    private final ITokenGenerationService tokenGenerationService;

    @Autowired
    public TMSController(ITokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService;
    }

    @GetMapping(value = "/generateToken/{applicant}")
    public String generateToken(@PathVariable("applicant") String applicant) {
        Optional<AssignedToken> assignedTokenOptional = tokenGenerationService.generateToken(new Applicant(applicant), new ApplicantDocument(applicant));
        if(assignedTokenOptional.isPresent()) {
            AssignedToken assignedToken = assignedTokenOptional.get();
            Token token = assignedToken.getToken();
            IServiceCounter serviceCounter = assignedToken.getServiceCounter();
            serviceCounter.serveToken(token);
            return "Token generated : " + token.getTokenNumber() +
                    " Assigned to counter no: "+serviceCounter.getServiceCounterNo();
        } else {
            return "Token generation failed, check documents...";
        }
    }

    @GetMapping(value = "/generatePremiumToken/{applicant}")
    public String generatePremiumToken(@PathVariable("applicant") String applicant) {
        Optional<AssignedToken> assignedToken = tokenGenerationService.generatePremiumToken(new Applicant(applicant), new ApplicantDocument(applicant));
        if(assignedToken.isPresent()) {
            return "Token generated : " + assignedToken.get().getToken().getTokenNumber() +
                    " Assigned to counter no: "+assignedToken.get().getServiceCounter().getServiceCounterNo();
        } else {
            return "Token generation failed, check documents...";
        }
    }
}
