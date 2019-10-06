package com.brillio.tms.tokenGeneration;

public class DocumentVerificationService implements IDocumentVerificationService {

    @Override
    public VerificationStatus verifyDocuments(Applicant applicant, ApplicantDocument document) {

        System.out.println("Verifying documents...");
        try {
            Thread.sleep(1000);
            return applicant.getApplicantName().equals(document.getApplicantName()) ?
                    VerificationStatus.SUCCESS : VerificationStatus.FAILURE;
        } catch (InterruptedException e) {
            System.out.println("Documents verification process failed, try later");
            return VerificationStatus.FAILURE;
        }
    }
}
