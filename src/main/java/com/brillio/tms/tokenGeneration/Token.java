package com.brillio.tms.tokenGeneration;

public class Token {

    public static final Token EMPTY_TOKEN = new Token(-1, null);
    private final int tokenNumber;
    private final TokenCategory tokenCategory;

    public Token(int tokenNumber, TokenCategory tokenCategory) {
        this.tokenNumber = tokenNumber;
        this.tokenCategory = tokenCategory;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public TokenCategory getTokenCategory() {
        return tokenCategory;
    }
}
