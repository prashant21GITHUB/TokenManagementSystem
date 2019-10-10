package com.brillio.tms.tokenGeneration;

public enum TokenCategory {
    NORMAL,
    PREMIUM;

    public static TokenCategory parse(String input) {
        for(TokenCategory category : TokenCategory.values()) {
            if(category.name().equalsIgnoreCase(input)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Token category must be either : NORMAL or PREMIUM");
    }
}
