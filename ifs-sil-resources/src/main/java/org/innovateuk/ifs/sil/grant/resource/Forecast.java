package org.innovateuk.ifs.sil.grant.resource;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Forecast {
    private String costCategory;
    private LocalDate start;
    private LocalDate end;
    private BigDecimal value;

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

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Forecast value(BigDecimal value) {
        setValue(value);
        return this;
    }
}
