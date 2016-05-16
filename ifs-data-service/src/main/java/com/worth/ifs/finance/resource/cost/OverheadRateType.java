package com.worth.ifs.finance.resource.cost;

public enum OverheadRateType{
    NONE(null),
    DEFAULT_PERCENTAGE(20),
    CUSTOM_RATE(null);

    private final Integer rate;

    OverheadRateType(Integer rate) {
        this.rate = rate;
    }

    public Integer getRate() {
        return rate;
    }
}
