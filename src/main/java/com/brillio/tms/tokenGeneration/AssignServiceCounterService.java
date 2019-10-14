package com.brillio.tms.tokenGeneration;

import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantTokenRecord;
import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;
import com.brillio.tms.tokenService.IServiceCounter;
import com.brillio.tms.tokenService.ServiceCounterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 *  It assigns a Service counter to the requested token with the help of {@link ServiceCounterRegistry}
 *  See also {@link TokenGenerationServiceImpl#generateTokenAndAssignServiceCounter}
 */
@Service
public class AssignServiceCounterService implements IAssignServiceCounterService {

    private final ServiceCounterRegistry serviceCounterRegistry;
    private final Logger LOGGER = LoggerFactory.getLogger("TokenAssignmentService");

    @Autowired
    public AssignServiceCounterService(ServiceCounterRegistry serviceCounterRegistry,
                                       KafkaTemplate<String, ApplicantTokenRecord> kafkaTemplate ) {
        this.serviceCounterRegistry = serviceCounterRegistry;
    }

    @Override
    public AssignedToken assignToken(final Token token, final Applicant applicant, long requestId) {
        IServiceCounter serviceCounter = serviceCounterRegistry.getServiceCounterForToken(token);
        return new AssignedToken(token, serviceCounter);
    }
}
