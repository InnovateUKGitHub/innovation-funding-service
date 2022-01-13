package org.innovateuk.ifs.finance.resource.cost;

public enum OverheadRateType{
    NONE(0, null, "None"),
    DEFAULT_PERCENTAGE(20, "defaultPercentage", "Default %"),
    TOTAL(null, "total", "Custom Amount"),
    HORIZON_2020_TOTAL(null, "horizon-2020-total", "Total");

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