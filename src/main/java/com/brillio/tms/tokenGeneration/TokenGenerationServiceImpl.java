package com.brillio.tms.tokenGeneration;

import com.brillio.tms.IAppService;
import com.brillio.tms.TMSConfig;
import com.brillio.tms.annotation.AppService;
import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.models.*;
import com.brillio.tms.tokenService.IServiceCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  This service is responsible for :
 *  1 - Start token generation counters at application startup. (Token generation counters are simulated by thread pool)
 *     Use {@literal token.generation.counters.size} application property to define the no. of token generation
 *     counters(i.e. size of thread pool)
 *
 *  2 -Verify the applicant documents and generate a token number.
 *  3 -Request the {@link AssignServiceCounterService} to assign a service counter to this token
 *  4 -Send the token to kafka topic(queue name) corresponding to assigned service counter
 *
 *  See method: {@link TokenGenerationServiceImpl#generateTokenAndAssignServiceCounter}
 */

@AppService
@Service
public class TokenGenerationServiceImpl implements IAppService, ITokenGenerationService {

    private final int TOTAL_COUNTERS;

    private ExecutorService executorService;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final DocumentVerificationService documentVerificationService;
    private final TokenGenerator tokenGenerator;
    private final AssignServiceCounterService assignerService;
    private final KafkaTemplate<String, ApplicantTokenRecord> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger("TokenGenerationService");

    @Autowired
    public TokenGenerationServiceImpl(DocumentVerificationService documentVerificationService,
                                      TokenGenerator tokenGenerator,
                                      AssignServiceCounterService assignerService,
                                      TMSConfig config,
                                      KafkaTemplate<String, ApplicantTokenRecord> kafkaTemplate) {
        this.documentVerificationService = documentVerificationService;
        this.tokenGenerator = tokenGenerator;
        this.assignerService = assignerService;
        this.TOTAL_COUNTERS = config.getTokenGenerationCountersSize();
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Optional<AssignedToken> generateTokenAndAssignServiceCounter(Applicant applicant, ApplicantDocument document,
                                                                        TokenCategory tokenCategory, long requestId)
            throws DocumentVerificationException {
        try {
            VerificationStatus verificationStatus = documentVerificationService.verifyDocuments(applicant, document);
            if(verificationStatus.isSuccess()) {
                Future<AssignedToken> tokenFuture = executorService.submit(
                        () -> {
                            Token token =  tokenGenerator.generateToken(tokenCategory);
                            AssignedToken assignedToken = assignerService.assignToken(token, applicant, requestId);
                            LOGGER.info( "ReqId: "+ requestId+ ", Token generated: " + token.getTokenNumber() +
                                    ", Assigned counter: " + assignedToken.getServiceCounter().getName()+
                                    ", Kafka topic: "+ assignedToken.getServiceCounter().getQueueName());
                            sendTokenToServiceCounterKafkaTopic(token, applicant, assignedToken.getServiceCounter(), requestId);
                            return assignedToken;
                        }
                );
                return Optional.of(tokenFuture.get());
            } else {
                throw new DocumentVerificationException(verificationStatus.getErrorMessage());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void sendTokenToServiceCounterKafkaTopic(final Token token, final Applicant applicant, final IServiceCounter serviceCounter, long requestId) {
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

    @Override
    public void start() {
        if(!isStarted.get()) {
            executorService = Executors.newFixedThreadPool(TOTAL_COUNTERS, new ThreadFactory() {
                private int counter = 1;
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("TokenGenerationCounter_" + counter++);
                    return t;
                }
            });
            isStarted.set(true);
        }
    }

    @Override
    public void stop() {
        if(isStarted.get()) {
            executorService.shutdownNow();
            isStarted.set(false);
        }
    }
}
