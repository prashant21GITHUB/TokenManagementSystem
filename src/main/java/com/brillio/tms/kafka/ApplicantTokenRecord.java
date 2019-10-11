package com.brillio.tms.kafka;

import com.brillio.tms.tokenGeneration.Applicant;
import com.brillio.tms.tokenGeneration.Token;

import java.io.Serializable;

public class ApplicantTokenRecord implements Serializable {
    private Applicant applicant;
    private Token token;
    private String serviceCounter;

    public ApplicantTokenRecord(Applicant applicant, Token token, String serviceCounter) {
        this.applicant = applicant;
        this.token = token;
        this.serviceCounter = serviceCounter;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Token getToken() {
        return token;
    }

    public String getServiceCounter() {
        return serviceCounter;
    }
}
