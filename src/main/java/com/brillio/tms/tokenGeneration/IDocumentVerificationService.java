package com.brillio.tms.tokenGeneration;

import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;

/**
 * Implementation: {@link DocumentVerificationService}
 */
public interface IDocumentVerificationService {
    VerificationStatus verifyDocuments(Applicant applicant, ApplicantDocument document);
}
