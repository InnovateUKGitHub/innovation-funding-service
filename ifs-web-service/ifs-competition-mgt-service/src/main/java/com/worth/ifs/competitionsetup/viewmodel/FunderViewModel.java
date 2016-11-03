package com.worth.ifs.competitionsetup.viewmodel;

import com.worth.ifs.competition.resource.CompetitionFunderResource;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FunderViewModel {

    @NotEmpty(message = "{validation.additionalinfoform.fundername.required}")
    private String funder;

    @Min(value=0, message = "{validation.additionalinfoform.funderbudget.min}")
    @NotNull(message = "{validation.additionalinfoform.funderbudget.required}")
    @Digits(integer = 8, fraction = 2, message = "{validation.additionalinfoform.funderbudget.invalid}")
    private BigDecimal funderBudget;

    @NotNull
    private Boolean coFunder;

    public FunderViewModel() {

    }

    public FunderViewModel(CompetitionFunderResource funderResource) {
        this.setFunder(funderResource.getFunder());
        this.setFunderBudget(funderResource.getFunderBudget());
        this.setCoFunder(funderResource.getCoFunder());
    }

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
