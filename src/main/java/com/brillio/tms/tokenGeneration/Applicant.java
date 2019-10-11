package com.brillio.tms.tokenGeneration;

import java.io.Serializable;

public class Applicant implements Serializable {

    private String name;

    public Applicant(String name) {
        this.name = name;
    }

    public Applicant() {
    }

    public String getName() {
        return name;
    }
}
