package org.innovateuk.ifs.sil.grant.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Period {
    private int month;
    @JsonProperty("forecastValue")
    private long value;

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

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Period value(long value) {
        setValue(value);
        return this;
    }
}
