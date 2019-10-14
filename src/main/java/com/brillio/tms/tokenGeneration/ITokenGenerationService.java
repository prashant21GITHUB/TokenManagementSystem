package com.brillio.tms.tokenGeneration;

import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;
import com.brillio.tms.models.AssignedToken;

import java.util.Optional;

/**
 *  See implementation: {@link TokenGenerationServiceImpl}
 */
public interface ITokenGenerationService {
    Optional<AssignedToken> generateTokenAndAssignServiceCounter(Applicant applicant, ApplicantDocument document,
                                                                 TokenCategory tokenCategory, long requestId)
            throws DocumentVerificationException;
}
