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
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;


@Service
public class AssignServiceCounterService implements IAssignServiceCounterService {

    private final ServiceCounterRegistry serviceCounterRegistry;
    private final KafkaTemplate<String, ApplicantTokenRecord> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger("TokenAssignmentService");

    @Autowired
    public AssignServiceCounterService(ServiceCounterRegistry serviceCounterRegistry,
                                       KafkaTemplate<String, ApplicantTokenRecord> kafkaTemplate ) {
        this.serviceCounterRegistry = serviceCounterRegistry;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public AssignedToken assignToken(final Token token, final Applicant applicant, long requestId) {
        IServiceCounter serviceCounter = serviceCounterRegistry.getServiceCounterForToken(token);
        assignTokenToServiceCounter(token, applicant, serviceCounter, requestId);
        return new AssignedToken(token, serviceCounter);
    }

    private void assignTokenToServiceCounter(final Token token, final Applicant applicant, final IServiceCounter serviceCounter, long requestId) {
        ApplicantTokenRecord record = new ApplicantTokenRecord(applicant, token, serviceCounter.getName());
        String kafkaTopic = serviceCounter.getQueueName();
        kafkaTemplate.send(kafkaTopic, record).addCallback(new ListenableFutureCallback<SendResult<String, ApplicantTokenRecord>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("Failed to send msg to kafka server: " + token +", RequestId: "+ requestId+", Error: "+throwable);
            }

            @Override
            public void onSuccess(@Nullable SendResult<String, ApplicantTokenRecord> stringStringSendResult) {
            }
        });
    }
}
