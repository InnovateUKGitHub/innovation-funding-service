package com.worth.ifs.competitionsetup.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Funder {

    @NotEmpty(message = "Please enter a funder name")
    private String funder;

    @Min(value=0, message = "Please a valid number.")
    @NotNull(message = "Please enter a budget")
    private BigDecimal funderBudget;

    private Boolean coFunder;

    public String getFunder() {
        return funder;
    }

    public void setFunder(String funder) {
        this.funder = funder;
    }

    public BigDecimal getFunderBudget() {
        return funderBudget;
    }

    public void setFunderBudget(BigDecimal funderBudget) {
        this.funderBudget = funderBudget;
    }

    public Boolean getCoFunder() {
        return coFunder;
    }

    public void setCoFunder(Boolean coFunder) {
        this.coFunder = coFunder;
    }
}
