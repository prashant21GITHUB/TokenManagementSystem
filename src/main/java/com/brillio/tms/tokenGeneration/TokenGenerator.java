package com.brillio.tms.tokenGeneration;

import com.brillio.tms.enums.TokenCategory;
import com.brillio.tms.models.Token;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TokenGenerator implements ITokenGenerator {

    private AtomicInteger nextCounter = new AtomicInteger(1);

    @Override
    public Token generateToken(TokenCategory tokenCategory) {
        return new Token(nextCounter.getAndIncrement(), tokenCategory);
    }
}
