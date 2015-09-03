package com.worth.ifs.resource;

public class Overhead {
    String acceptRate;
    Integer customRate;

    public Overhead() {
    }

    public Overhead(String acceptRate, Integer customRate) {
        this.acceptRate = acceptRate;
        this.customRate = customRate;
    }

    public String getAcceptRate() {
        return acceptRate;
    }

    public Integer getCustomRate() {
        return customRate;
    }
}
