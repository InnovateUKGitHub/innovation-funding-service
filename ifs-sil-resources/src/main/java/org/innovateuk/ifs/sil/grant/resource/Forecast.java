package org.innovateuk.ifs.sil.grant.resource;

import java.time.LocalDate;

public class Forecast {
    private String costCategory;
    private LocalDate start;
    private LocalDate end;
    private double value;

    public String getCostCategory() {
        return costCategory;
    }

    public void setCostCategory(String costCategory) {
        this.costCategory = costCategory;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public Forecast start(LocalDate start) {
        setStart(start);
        return this;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Forecast end(LocalDate end) {
        setEnd(end);
        return this;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Forecast value(double value) {
        setValue(value);
        return this;
    }
}
