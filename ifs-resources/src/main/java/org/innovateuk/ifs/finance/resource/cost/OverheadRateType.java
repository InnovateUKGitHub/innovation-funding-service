package org.innovateuk.ifs.finance.resource.cost;

public enum OverheadRateType{
    NONE(null, "None"),
    DEFAULT_PERCENTAGE(20, "Default %"),
    TOTAL(null, "Custom Amount"),
    CUSTOM_RATE(null, "customRate"); // NOTE: Now that competiton 1 is gone, there is no need to keep custom rate. TODO: Check with Rogier and remove if safe : INFUND-7322

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
