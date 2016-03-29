package com.worth.ifs.application.finance.model;

import java.math.BigDecimal;

public class AcademicFinanceFormField {
    Long id;
    String value;
    BigDecimal calculatedValue;

    public AcademicFinanceFormField(Long id, String value, BigDecimal calculatedValue) {
        this.id = id;
        this.value = value;
        this.calculatedValue = calculatedValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BigDecimal getCalculatedValue() {
        return calculatedValue;
    }

    public void setCalculatedValue(BigDecimal calculatedValue) {
        this.calculatedValue = calculatedValue;
    }
}
