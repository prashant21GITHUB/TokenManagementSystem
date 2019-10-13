package com.brillio.tms.tokenGeneration;

import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;
import com.brillio.tms.models.AssignedToken;

import java.util.Optional;

public interface ITokenGenerationService {
    Optional<AssignedToken> generateToken(Applicant applicant, ApplicantDocument document) throws DocumentVerificationException;
    Optional<AssignedToken> generatePremiumToken(Applicant applicant, ApplicantDocument document) throws DocumentVerificationException;
}
