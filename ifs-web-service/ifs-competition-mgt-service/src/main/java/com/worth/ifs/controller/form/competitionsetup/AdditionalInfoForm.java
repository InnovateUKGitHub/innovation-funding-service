package com.worth.ifs.controller.form.competitionsetup;

/**
 * Form for the additional info competition setup section.
 */
public class AdditionalInfoForm extends CompetitionSetupForm {
    private String activityCode;
    private String innovateBudget;
    private String coFunders;
    private String coFundersBudget;

    public AdditionalInfoForm() {
    }

    public AdditionalInfoForm(String activityCode, String innovateBudget, String coFunders, String coFundersBudget) {
        this.activityCode = activityCode;
        this.innovateBudget = innovateBudget;
        this.coFunders = coFunders;
        this.coFundersBudget = coFundersBudget;
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

    public String getCoFunders() {
        return coFunders;
    }

    public void setCoFunders(String coFunders) {
        this.coFunders = coFunders;
    }

    public String getCoFundersBudget() {
        return coFundersBudget;
    }

    public void setCoFundersBudget(String coFundersBudget) {
        this.coFundersBudget = coFundersBudget;
    }
}
