package com.brillio.tms.tokenGeneration;

import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;
import org.springframework.stereotype.Service;

/**
 *  Verifies the document before generating a token number.
 *  Document verification fails if the applicant name and the name present in document mismatch.
 *  See {@link Applicant} and {@link ApplicantDocument}
 */
@Service
public class DocumentVerificationService implements IDocumentVerificationService {

    @Override
    public VerificationStatus verifyDocuments(Applicant applicant, ApplicantDocument document) {
        if(document.getDocumentNum() == null || document.getDocumentNum() < 1) {
            return new VerificationStatus(false, "Invalid documents, document num is missing or not valid");
        }
        if(applicant.getName().equalsIgnoreCase(document.getApplicantName())) {
            return new VerificationStatus(true, "");
        } else {
            return new VerificationStatus(false, "Invalid documents, applicant name mismatch");
        }
    }
}
