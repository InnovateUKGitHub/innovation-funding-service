package org.innovateuk.ifs.finance.resource.cost;

public enum OverheadRateType{
    NONE(null, null),
    DEFAULT_PERCENTAGE(20, null),
    TOTAL(null, "total"),
    CUSTOM_RATE(null, "customRate");

    private final Integer rate;
    private final String name;

    OverheadRateType(Integer rate, String name) {
        this.rate = rate;
        this.name = name;
    }

    public Integer getRate() {
        return rate;
    }
    public String getName() {
        return name;
    }
}
