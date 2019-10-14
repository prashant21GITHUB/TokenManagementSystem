package com.brillio.tms.tokenGeneration;

import com.brillio.tms.IAppService;
import com.brillio.tms.TMSConfig;
import com.brillio.tms.annotation.AppService;
import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.exceptions.DocumentVerificationException;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;
import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@AppService
@Service
public class TokenGenerationServiceImpl implements IAppService, ITokenGenerationService {

    private final int TOTAL_COUNTERS;

    private ExecutorService executorService;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final DocumentVerificationService documentVerificationService;
    private final TokenGenerator tokenGenerator;
    private final AssignServiceCounterService assignerService;
    private final Logger LOGGER = LoggerFactory.getLogger("TokenGenerationService");

    @Autowired
    public TokenGenerationServiceImpl(DocumentVerificationService documentVerificationService,
                                      TokenGenerator tokenGenerator,
                                      AssignServiceCounterService assignerService,
                                      TMSConfig config) {
        this.documentVerificationService = documentVerificationService;
        this.tokenGenerator = tokenGenerator;
        this.assignerService = assignerService;
        this.TOTAL_COUNTERS = config.getTokenGenerationCountersSize();
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
                                    ", Assigned counter: " + assignedToken.getServiceCounter().getName());
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
