package com.worth.ifs.application.finance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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
    @JsonIgnore
    public String getNumberValue() {
        if(value == null){
            return "";
        }
        DecimalFormat formatter = new DecimalFormat("##0.##");
        formatter.setMaximumFractionDigits(2);
        BigDecimal numberValue = new BigDecimal(value);
        return formatter.format(numberValue);
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
