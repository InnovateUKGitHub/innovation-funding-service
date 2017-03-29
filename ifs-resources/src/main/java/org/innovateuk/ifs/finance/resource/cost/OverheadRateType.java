package org.innovateuk.ifs.finance.resource.cost;

public enum OverheadRateType{
    NONE(null, "NONE", "None"),
    DEFAULT_PERCENTAGE(20, "DEFAULT_PERCENTAGE", "Default %"),
    CUSTOM_AMOUNT(null, "CUSTOM_AMOUNT", "Custom Amount"),
    CUSTOM_RATE(null, "CUSTOM_RATE", "Custom Rate"); // NOTE: Now that competiton 1 is gone, there is no need to keep custom rate. TODO: Check with Rogier and remove if safe : INFUND-7322

    private final Integer rate;
    private final String name;
    private final String label;

    OverheadRateType(Integer rate, String name, String label) {
        this.rate = rate;
        this.name = name;
        this.label = label;
    }

    public Integer getRate() {
        return rate;
    }
    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }
}