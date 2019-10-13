package com.brillio.tms.tokenGeneration;

import com.brillio.tms.enums.VerificationStatus;
import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;
import org.springframework.stereotype.Service;

@Service
public class DocumentVerificationService implements IDocumentVerificationService {

    @Override
    public VerificationStatus verifyDocuments(Applicant applicant, ApplicantDocument document)
            throws DocumentVerificationException {
        if(document.getDocumentNum() == null || document.getDocumentNum() < 1) {
            throw new DocumentVerificationException("Invalid documents, document num is missing or not valid");
        }
        if(applicant.getName().equals(document.getApplicantName())) {
            return VerificationStatus.SUCCESS;
        }
        throw new DocumentVerificationException("Invalid documents, applicant name mismatch");
    }
}
