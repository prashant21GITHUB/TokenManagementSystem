package com.brillio.tms.tokenGeneration;

public interface IDocumentVerificationService {
    VerificationStatus verifyDocuments(Applicant applicant, ApplicantDocument document);
}
