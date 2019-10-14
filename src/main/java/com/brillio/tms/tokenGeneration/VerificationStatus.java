package com.brillio.tms.tokenGeneration;

public class  VerificationStatus {
    private final boolean success;
    private final String errorMessage;

    public VerificationStatus(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
