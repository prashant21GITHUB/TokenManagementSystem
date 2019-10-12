package com.brillio.tms.tokenGeneration;

import com.brillio.tms.annotation.AppService;
import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.enums.VerificationStatus;
import com.brillio.tms.models.Applicant;
import com.brillio.tms.models.ApplicantDocument;
import com.brillio.tms.models.AssignedToken;
import com.brillio.tms.models.Token;
import com.brillio.tms.tokenService.AssignServiceCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@AppService
@Service
public class TokenGenerationServiceImpl implements IAppService, ITokenGenerationService {

    private static final int TOTAL_COUNTERS = 4;

    private ExecutorService executorService;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final DocumentVerificationService documentVerificationService;
    private final TokenGenerator tokenGenerator;
    private final AssignServiceCounterService assignerService;

    @Autowired
    public TokenGenerationServiceImpl(DocumentVerificationService documentVerificationService,
                                      TokenGenerator tokenGenerator,
                                      AssignServiceCounterService assignerService) {
        this.documentVerificationService = documentVerificationService;
        this.tokenGenerator = tokenGenerator;
        this.assignerService = assignerService;
    }

    @Override
    public Optional<AssignedToken> generateToken(Applicant applicant,
                                                 ApplicantDocument document) {
        return generateTokenAndAssignServiceCounter(applicant, document, TokenCategory.NORMAL);
    }

    @Override
    public Optional<AssignedToken> generatePremiumToken(Applicant applicant,
                                                        ApplicantDocument document) {
        return generateTokenAndAssignServiceCounter(applicant, document, TokenCategory.PREMIUM);
    }

    private Optional<AssignedToken> generateTokenAndAssignServiceCounter(Applicant applicant, ApplicantDocument document,
                                                                         TokenCategory tokenCategory) {
        Optional<Token> tokenOptional = generateNextToken(applicant, document, tokenCategory);
        if(tokenOptional.isPresent()) {
            AssignedToken assignedToken = assignerService.assignToken(tokenOptional.get(), applicant);
            return Optional.of(assignedToken);
        }
        return Optional.empty();
    }

    private Optional<Token> generateNextToken(Applicant applicant, ApplicantDocument document, TokenCategory tokenCategory) {
        Optional<Token> tokenOptional = Optional.empty();
        try {
            VerificationStatus verificationStatus =
                    documentVerificationService.verifyDocuments(applicant, document);
            if(VerificationStatus.SUCCESS.equals(verificationStatus)) {
                Future<Token> tokenFuture = executorService.submit(
                        () -> {
                            System.out.println("Token generated on counter: " + Thread.currentThread().getName());
                            return tokenGenerator.generateToken(tokenCategory);
                        }
                );
                tokenOptional = Optional.of(tokenFuture.get());
            } else {
                System.out.println("Invalid documents");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            return tokenOptional;
        }
    }

    @Override
    public void start() throws InterruptedException {
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
