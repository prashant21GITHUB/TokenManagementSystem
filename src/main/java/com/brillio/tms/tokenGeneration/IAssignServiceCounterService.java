package com.brillio.tms.tokenGeneration;

import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;

public interface IAssignServiceCounterService {
    AssignedToken assignToken(Token token, Applicant applicant, long requestId);
}
