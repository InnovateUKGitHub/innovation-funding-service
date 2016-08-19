package com.worth.ifs.competitionsetup.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Form for the additional info competition setup section.
 */
public class AdditionalInfoForm extends CompetitionSetupForm {

    @Size(max = 255, message = "Activity code has a maximum length of 255 characters")
    @NotEmpty(message = "Please enter an activity code")
    private String activityCode;

    @Size(max = 255, message = "{validation.additionalinfoform.innovatebudget.size}")
    private String innovateBudget;

    @Size(max = 255, message = "Funder has a maximum length of 255 characters")
    @NotEmpty(message = "Please enter a funder name")
    private String funder;

    @Min(value=0, message = "Please a valid number.")
    @NotNull(message = "Please enter a budget")
    private BigDecimal funderBudget;

    @NotEmpty(message = "{validation.additionalinfoform.pafnumber.required}")
     private String pafNumber;

    @NotEmpty(message = "{validation.additionalinfoform.competitioncode.required}")
    private String competitionCode;

    @NotEmpty(message = "{validation.additionalinfoform.budgetcode.required}")
    private String budgetCode;

    @Valid
    private List<CoFunderForm> coFunders = new ArrayList<>();


    public AdditionalInfoForm() {
    }

    public AdditionalInfoForm(String activityCode, String innovateBudget, String funder, BigDecimal funderBudget) {
        this.activityCode = activityCode;
        this.innovateBudget = innovateBudget;
        this.funder = funder;
        this.funderBudget = funderBudget;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getInnovateBudget() {
        return innovateBudget;
    }

    public void setInnovateBudget(String innovateBudget) {
        this.innovateBudget = innovateBudget;
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

    public String getPafNumber() {
        return pafNumber;
    }

    public void setPafNumber(String pafNumber) {
        this.pafNumber = pafNumber;
    }

    public String getCompetitionCode() {
        return competitionCode;
    }

    public void setCompetitionCode(String competitionCode) {
        this.competitionCode = competitionCode;
    }

    public String getBudgetCode() {
        return budgetCode;
    }

    public void setBudgetCode(String budgetCode) {
        this.budgetCode = budgetCode;
    }

    public List<CoFunderForm> getCoFunders() {
        return coFunders;
    }

    public void setCoFunders(List<CoFunderForm> coFunders) {
        this.coFunders = coFunders;
    }

    public int getCoFundersCount() {
        return coFunders.size();
    }

    public Double getTotalFunding() {
        double totalFunding = coFunders.stream().filter(o -> o.getCoFunderBudget() != null).mapToDouble(o -> o.getCoFunderBudget().doubleValue()).sum();
        return funderBudget != null ? (totalFunding + funderBudget.doubleValue()) : totalFunding;
    }
}
