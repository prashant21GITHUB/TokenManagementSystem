package com.brillio.tms.tokenGeneration;

import com.brillio.tms.tokenService.TokenServiceCounterAssignerService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
@Named(value = "ITokenGenerationService")
public class TokenGenerationServiceImpl implements IAppService, ITokenGenerationService {

    private static final int TOTAL_COUNTERS = 4;

    private ExecutorService executorService;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final DocumentVerificationService documentVerificationService;
    private final TokenGenerator tokenGenerator;
    private final TokenServiceCounterAssignerService assignerService;

    @Inject
    public TokenGenerationServiceImpl(DocumentVerificationService documentVerificationService,
                                      TokenGenerator tokenGenerator,
                                      TokenServiceCounterAssignerService assignerService) {
        this.documentVerificationService = documentVerificationService;
        this.tokenGenerator = tokenGenerator;
        this.assignerService = assignerService;
    }

    @Override
    public Optional<AssignedToken> generateToken(Applicant applicant,
                                                 ApplicantDocument document) {
        return generateNextToken(applicant, document, TokenCategory.NORMAL);
    }

    @Override
    public Optional<AssignedToken> generatePremiumToken(Applicant applicant,
                                                        ApplicantDocument document) {
        return generateNextToken(applicant, document, TokenCategory.PREMIUM);
    }

    private Optional<AssignedToken> generateNextToken(Applicant applicant, ApplicantDocument document, TokenCategory tokenCategory) {
        Optional<AssignedToken> tokenOptional = Optional.empty();
        try {
            VerificationStatus verificationStatus =
                    documentVerificationService.verifyDocuments(applicant, document);
            if(VerificationStatus.SUCCESS.equals(verificationStatus)) {
                Token token = executorService.submit(
                        () -> tokenGenerator.generateToken(tokenCategory)
                ).get();
                tokenOptional = Optional.of(assignerService.assignToken(token));
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
