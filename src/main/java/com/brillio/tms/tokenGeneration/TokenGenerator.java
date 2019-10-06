package com.brillio.tms.tokenGeneration;

import java.util.concurrent.atomic.AtomicInteger;

public class TokenGenerator implements ITokenGenerator {

    private AtomicInteger nextCounter = new AtomicInteger(1);

    @Override
    public Token generateToken(TokenCategory tokenCategory) {
        return new Token(nextCounter.getAndIncrement(), tokenCategory);
    }
}
