package com.brillio.tms;

import com.brillio.tms.tokenGeneration.*;
import com.brillio.tms.tokenService.IServiceCounterRegistry;
import com.brillio.tms.tokenService.ITokenServiceCounterAssignerService;
import com.brillio.tms.tokenService.ServiceCounterRegistry;
import com.brillio.tms.tokenService.TokenServiceCounterAssignerService;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class TMSModule extends AbstractModule {

    @Override
    public void configure() {
        bind(IServiceCounterRegistry.class).to(ServiceCounterRegistry.class).in(Scopes.SINGLETON);
        bind(ITokenGenerationService.class).to(TokenGenerationServiceImpl.class).in(Scopes.SINGLETON);
        bind(IDocumentVerificationService.class).to(DocumentVerificationService.class).in(Scopes.SINGLETON);
        bind(ITokenGenerator.class).to(TokenGenerator.class).in(Scopes.SINGLETON);
        bind(ITokenServiceCounterAssignerService.class).to(TokenServiceCounterAssignerService.class).in(Scopes.SINGLETON);

        bind(IAppService.class).annotatedWith(Names.named("ITokenGenerationService"))
                .to(TokenGenerationServiceImpl.class).in(Scopes.SINGLETON);
    }
}
