package com.worth.ifs.competitionsetup.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by skistapur on 18/07/2016.
 */
public class CoFunderForm {

    @NotEmpty(message = "Please enter a funder name")
    private String coFunder;

    @Min(value=0, message = "Please a valid number.")
    @NotNull(message = "Please enter a budget")
    private BigDecimal coFunderBudget;

    public String getCoFunder() {
        return coFunder;
    }

    public void setCoFunder(String coFunder) {
        this.coFunder = coFunder;
    }

    public BigDecimal getCoFunderBudget() {
        return coFunderBudget;
    }

    public void setCoFunderBudget(BigDecimal coFunderBudget) {
        this.coFunderBudget = coFunderBudget;
    }
}
