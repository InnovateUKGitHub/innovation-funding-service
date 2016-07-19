package com.worth.ifs.competitionsetup.form;

import javax.validation.constraints.Min;

/**
 * Created by skistapur on 18/07/2016.
 */
public class CoFunderForm {

    private String coFunder;
    @Min(value=0, message = "Please a valid number.")
    private Double coFunderBudget;

    public String getCoFunder() {
        return coFunder;
    }

    public void setCoFunder(String coFunder) {
        this.coFunder = coFunder;
    }

    public Double getCoFunderBudget() {
        return coFunderBudget;
    }

    public void setCoFunderBudget(Double coFunderBudget) {
        this.coFunderBudget = coFunderBudget;
    }
}
