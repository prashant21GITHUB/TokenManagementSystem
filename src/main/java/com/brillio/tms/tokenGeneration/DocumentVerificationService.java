package com.brillio.tms.tokenGeneration;

import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;
import org.springframework.stereotype.Service;

@Service
public class DocumentVerificationService implements IDocumentVerificationService {

    @Override
    public VerificationStatus verifyDocuments(Applicant applicant, ApplicantDocument document) {
        if(document.getDocumentNum() == null || document.getDocumentNum() < 1) {
            return new VerificationStatus(false, "Invalid documents, document num is missing or not valid");
        }
        if(applicant.getName().equals(document.getApplicantName())) {
            return new VerificationStatus(true, "");
        } else {
            return new VerificationStatus(false, "Invalid documents, applicant name mismatch");
        }
    }
}
