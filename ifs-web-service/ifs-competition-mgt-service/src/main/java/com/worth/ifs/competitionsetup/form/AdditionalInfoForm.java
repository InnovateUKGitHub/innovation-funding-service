package com.worth.ifs.competitionsetup.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Form for the additional info competition setup section.
 */
public class AdditionalInfoForm extends CompetitionSetupForm {
    @Size(max = 255, message = "Activity code has a maximum length of 255 characters")
    private String activityCode;
    @Size(max = 255, message = "Innovate budget has a maximum length of 255 characters")
    private String innovateBudget;
    @Size(max = 255, message = "Funder has a maximum length of 255 characters")
    private String funder;

    //TODO add validation for numeric
    //@Size(max = 255, message = "Funder budget has a maximum length of 255 characters")
    private Double funderBudget;
    @NotEmpty(message = "Please enter a PAF number")
    private String pafNumber;
    @NotEmpty(message = "Please generate a competition code")
    private String competitionCode;
    @NotEmpty(message = "Please enter a budget code")
    private String budgetCode;


    private List<CoFunderForm> coFunders = new ArrayList<>();


    public AdditionalInfoForm() {
    }

    public AdditionalInfoForm(String activityCode, String innovateBudget, String funder, Double funderBudget) {
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

    public Double getFunderBudget() {
        return funderBudget;
    }

    public void setFunderBudget(Double funderBudget) {
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
}
