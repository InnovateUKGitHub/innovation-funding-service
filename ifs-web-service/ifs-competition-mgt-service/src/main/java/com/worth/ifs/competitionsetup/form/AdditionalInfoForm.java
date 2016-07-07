package com.worth.ifs.competitionsetup.form;

import javax.validation.constraints.Size;

/**
 * Form for the additional info competition setup section.
 */
public class AdditionalInfoForm extends CompetitionSetupForm {
    @Size(max = 255, message = "Activity code has a maximum length of 255 characters")
    private String activityCode;
    @Size(max = 255, message = "Innovate budget has a maximum length of 255 characters")
    private String innovateBudget;
    @Size(max = 255, message = "Co-funders has a maximum length of 255 characters")
    private String coFunders;
    @Size(max = 255, message = "Co-funders budget has a maximum length of 255 characters")
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
