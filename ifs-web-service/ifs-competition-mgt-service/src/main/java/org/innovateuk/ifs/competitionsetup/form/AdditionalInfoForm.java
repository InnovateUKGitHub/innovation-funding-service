package org.innovateuk.ifs.competitionsetup.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Form for the additional info competition setup section.
 */
public class AdditionalInfoForm extends CompetitionSetupForm {

    @Size(max = 255, message = "{validation.additionalinfoform.activitycode.size}")
    private String activityCode;

    private String pafNumber;

    @NotEmpty(message = "{validation.additionalinfoform.competitioncode.required}")
    private String competitionCode;

    private String budgetCode;

    @Valid
    @NotEmpty(message = "Please enter a funder")
    private List<FunderRowForm> funders = new ArrayList<>();

    public AdditionalInfoForm() {
    }

    public AdditionalInfoForm(String pafNumber, String activityCode, String budgetCode, List<FunderRowForm> funders) {
        this.activityCode = activityCode;
        this.funders = funders;
        this.budgetCode = budgetCode;
        this.pafNumber = pafNumber;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
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

    public List<FunderRowForm> getFunders() {
        return funders;
    }

    public void setFunders(List<FunderRowForm> funders) {
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
