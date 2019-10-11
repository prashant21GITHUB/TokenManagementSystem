package com.brillio.tms.controllers;

import com.brillio.tms.tokenGeneration.Applicant;
import com.brillio.tms.tokenGeneration.ApplicantDocument;

import java.io.Serializable;

public class GenerateTokenRequest implements Serializable {

    private Applicant applicant;
    private ApplicantDocument document;

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public ApplicantDocument getDocument() {
        return document;
    }

    public void setDocument(ApplicantDocument document) {
        this.document = document;
    }
}
