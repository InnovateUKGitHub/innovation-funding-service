package com.worth.ifs.sil.experian.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private boolean checkPassed;
    private String iban;
    private List<Condition> conditions;

    public ValidationResult() {
        this.conditions = new ArrayList<>();
    }

    public ValidationResult(boolean checkPassed, String iban, List<Condition> conditions) {
        this.checkPassed = checkPassed;
        this.iban = iban;
        this.conditions = conditions;
    }

    public boolean isCheckPassed() {
        return checkPassed;
    }

    public void setCheckPassed(boolean checkPassed) {
        this.checkPassed = checkPassed;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ValidationResult that = (ValidationResult) o;

        return new EqualsBuilder()
                .append(checkPassed, that.checkPassed)
                .append(iban, that.iban)
                .append(conditions, that.conditions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(checkPassed)
                .append(iban)
                .append(conditions)
                .toHashCode();
    }
}
