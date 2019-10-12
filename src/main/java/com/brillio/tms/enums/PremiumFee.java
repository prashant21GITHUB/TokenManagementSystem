package com.brillio.tms.enums;

public enum  PremiumFee {

    NORMAL_FEE(0.0),
    PREMIUM_FEE(5.0);

    private final double fee;

    PremiumFee(double fee) {
        this.fee = fee;
    }

    public double getFee() {
        return fee;
    }
}
