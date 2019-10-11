package com.brillio.tms.controllers;

import com.brillio.tms.tokenGeneration.Applicant;
import com.brillio.tms.tokenGeneration.Token;

public class GenerateTokenResponse {
    private Token token;
    private Applicant applicant;
    private String serviceCounter;

    public GenerateTokenResponse(Applicant applicant) {
        this.applicant = applicant;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public String getServiceCounter() {
        return serviceCounter;
    }

    public void setServiceCounter(String serviceCounter) {
        this.serviceCounter = serviceCounter;
    }
}
