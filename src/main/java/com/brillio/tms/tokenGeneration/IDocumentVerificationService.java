package com.brillio.tms.tokenGeneration;

import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;

public interface IDocumentVerificationService {
    VerificationStatus verifyDocuments(Applicant applicant, ApplicantDocument document);
}
