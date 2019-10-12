package com.brillio.tms.controllers;

import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;

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
