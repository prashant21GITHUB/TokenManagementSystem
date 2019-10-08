//package com.brillio.tms;
//
//import com.brillio.tms.tokenGeneration.*;
//import com.brillio.tms.tokenService.IServiceCounter;
//import com.google.inject.Guice;
//import com.google.inject.Injector;
//import com.google.inject.Key;
//import com.google.inject.name.Names;
//
//import java.util.Optional;
//
//public class RunTMS {
//
//    public static void main(String[] args) {
//        Injector injector = Guice.createInjector(new TMSModule());
//        startService(ITokenGenerationService.class, injector);
//        ITokenGenerationService tokenGenerationService = injector.getInstance(ITokenGenerationService.class);
//        while (true) {
//            Applicant applicant1 = new Applicant("Prashant");
//            ApplicantDocument document1 = new ApplicantDocument("Prashant");
//            Optional<AssignedToken> assignedTokenOptional = tokenGenerationService.generateToken(applicant1, document1);
//            if(assignedTokenOptional.isPresent()) {
//                AssignedToken assignedToken = assignedTokenOptional.get();
//                Token token = assignedToken.getToken();
//                IServiceCounter serviceCounter = assignedToken.getServiceCounter();
//                serviceCounter.serveToken(token);
//            }
//
//            Applicant applicant2 = new Applicant("Bajpai");
//            ApplicantDocument document2 = new ApplicantDocument("Bajpai");
//            assignedTokenOptional = tokenGenerationService.generatePremiumToken(applicant2, document2);
//            if(assignedTokenOptional.isPresent()) {
//                AssignedToken assignedToken = assignedTokenOptional.get();
//                Token token = assignedToken.getToken();
//                IServiceCounter serviceCounter = assignedToken.getServiceCounter();
//                serviceCounter.serveToken(token);
//            }
//        }
//
//    }
//
//    private static void startService(Class<ITokenGenerationService> serviceClass, Injector injector) {
//        IAppService instance = injector.getInstance(Key.get(IAppService.class, Names.named("ITokenGenerationService")));
//        instance.start();
//    }
//}
