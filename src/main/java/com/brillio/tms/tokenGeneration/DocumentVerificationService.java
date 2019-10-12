package com.brillio.tms.tokenGeneration;

import com.brillio.tms.enums.VerificationStatus;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;
import org.springframework.stereotype.Service;

@Service
public class DocumentVerificationService implements IDocumentVerificationService {

    @Override
    public VerificationStatus verifyDocuments(Applicant applicant, ApplicantDocument document) {
//        System.out.println("Verifying documents...");
        try {
            return applicant.getName().equals(document.getApplicantName()) ?
                    VerificationStatus.SUCCESS : VerificationStatus.FAILURE;
        } catch (Exception e) {
            System.out.println("Documents verification process failed, try later");
            return VerificationStatus.FAILURE;
        }
    }
}
