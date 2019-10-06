package com.brillio.tms.tokenGeneration;

import java.util.Optional;

public interface ITokenGenerationService {
    Optional<AssignedToken> generateToken(Applicant applicant, ApplicantDocument document);
    Optional<AssignedToken> generatePremiumToken(Applicant applicant, ApplicantDocument document);
}
