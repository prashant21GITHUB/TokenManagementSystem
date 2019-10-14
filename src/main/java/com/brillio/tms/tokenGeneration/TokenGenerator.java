package com.brillio.tms.tokenGeneration;

import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.models.Token;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates a unique token number(counter based approach)
 * See {@link TokenGenerationServiceImpl#generateTokenAndAssignServiceCounter}
 */
@Component
public class TokenGenerator implements ITokenGenerator {

    private AtomicInteger nextCounter = new AtomicInteger(1);

    @Override
    public Token generateToken(TokenCategory tokenCategory) {
        return new Token(nextCounter.getAndIncrement(), tokenCategory);
    }
}
