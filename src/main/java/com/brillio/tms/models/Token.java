package com.brillio.tms.models;

import com.brillio.tms.enums.TokenCategory;

import java.io.Serializable;

public class Token implements Serializable {

    public static final Token EMPTY_TOKEN = new Token(-1, null);
    private int tokenNumber;
    private TokenCategory tokenCategory;

    public Token() {
    }

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
