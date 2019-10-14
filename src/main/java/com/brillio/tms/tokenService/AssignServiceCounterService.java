package com.brillio.tms.tokenService;

import com.brillio.tms.kafka.json.ObjectToJsonSerializer;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantTokenRecord;
import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;
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
    private final ObjectToJsonSerializer jsonSerializer;

    @Autowired
    public AssignServiceCounterService(ServiceCounterRegistry serviceCounterRegistry,
                                       KafkaTemplate<String, ApplicantTokenRecord> kafkaTemplate,
                                       ObjectToJsonSerializer jsonSerializer) {
        this.serviceCounterRegistry = serviceCounterRegistry;
        this.kafkaTemplate = kafkaTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public AssignedToken assignToken(final Token token, final Applicant applicant) {
        IServiceCounter serviceCounter = serviceCounterRegistry.getServiceCounterForToken(token);
        assignTokenToServiceCounter(token, applicant, serviceCounter);
        return new AssignedToken(token, serviceCounter);
    }

    private void assignTokenToServiceCounter(final Token token, final Applicant applicant, final IServiceCounter serviceCounter) {
        ApplicantTokenRecord record = new ApplicantTokenRecord(applicant, token, serviceCounter.getName());
        String kafkaTopic = serviceCounter.getQueueName();
        kafkaTemplate.send(kafkaTopic, record).addCallback(new ListenableFutureCallback<SendResult<String, ApplicantTokenRecord>>() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("Failed to send msg : " + token +", Error: "+throwable);
            }

            @Override
            public void onSuccess(@Nullable SendResult<String, ApplicantTokenRecord> stringStringSendResult) {
                System.out.println("Assigned: " + jsonSerializer.convertToJsonString(record));
            }
        });
    }
}
