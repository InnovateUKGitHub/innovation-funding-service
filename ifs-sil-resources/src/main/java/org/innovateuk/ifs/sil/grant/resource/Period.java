package org.innovateuk.ifs.sil.grant.resource;

import java.math.BigDecimal;

public class Period {
    private int month;
    private BigDecimal value;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Period month(int month) {
        setMonth(month);
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Period value(BigDecimal value) {
        setValue(value);
        return this;
    }
}
