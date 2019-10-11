package com.brillio.tms.tokenGeneration;

import java.io.Serializable;

public class ApplicantDocument implements Serializable {

    private String applicantName;
    private int documentNum;

    public ApplicantDocument(String applicantName, int documentNum) {
        this.applicantName = applicantName;
        this.documentNum = documentNum;
    }

    public ApplicantDocument() {
    }

    public String getApplicantName() {
        return applicantName;
    }

    public int getDocumentNum() {
        return documentNum;
    }
}
