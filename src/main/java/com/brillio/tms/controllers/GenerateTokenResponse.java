package com.brillio.tms.controllers;

import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.Token;

/**
 * Response object for HTTP POST request to generate token, See {@link TMSController#generateToken} ()}
 */
public class GenerateTokenResponse {
    private Token token;
    private Applicant applicant;
    private String serviceCounter;
    private String errorMessage = "";

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

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "{" +
                "token:" + token +
                ", applicant:" + applicant +
                ", serviceCounter:" + serviceCounter +
                ", errorMessage:'" + errorMessage + '\'' +
                '}';
    }
}
