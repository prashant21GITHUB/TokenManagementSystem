package com.brillio.tms.controllers;

import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;

import java.io.Serializable;

public class GenerateTokenRequest implements Serializable {

    private Applicant applicant;
    private ApplicantDocument document;
    private TokenCategory tokenCategory;

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

    public TokenCategory getTokenCategory() {
        return tokenCategory;
    }

    public void setTokenCategory(TokenCategory tokenCategory) {
        this.tokenCategory = tokenCategory;
    }
}
