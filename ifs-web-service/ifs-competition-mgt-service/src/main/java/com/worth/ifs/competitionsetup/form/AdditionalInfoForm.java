package com.worth.ifs.competitionsetup.form;

import com.worth.ifs.competitionsetup.viewmodel.FunderViewModel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Size;
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

    @NotEmpty(message = "{validation.additionalinfoform.pafnumber.required}")
     private String pafNumber;

    @NotEmpty(message = "{validation.additionalinfoform.competitioncode.required}")
    private String competitionCode;

    @NotEmpty(message = "{validation.additionalinfoform.budgetcode.required}")
    private String budgetCode;

    @Valid
    @NotEmpty(message = "Please enter a funder")
    private List<FunderViewModel> funders = new ArrayList<>();

    public AdditionalInfoForm() {
    }

    public AdditionalInfoForm(String activityCode, String innovateBudget, String budgetCode, List<FunderViewModel> funders) {
        this.activityCode = activityCode;
        this.innovateBudget = innovateBudget;
        this.funders = funders;
        this.budgetCode = budgetCode;
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

    public List<FunderViewModel> getFunders() {
        return funders;
    }

    public void setFunders(List<FunderViewModel> funders) {
        this.funders = funders;
    }

    public int getFundersCount() {
        return funders.size();
    }

    public Double getTotalFunding() {
        return funders.stream()
                .filter(o -> o.getFunderBudget() != null)
                .mapToDouble(o -> o.getFunderBudget().doubleValue())
                .sum();
    }
}
