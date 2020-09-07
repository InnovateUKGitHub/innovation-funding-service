package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.Justification;

import javax.validation.constraints.NotNull;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.NOT_BLANK_MESSAGE;

public class JustificationForm {

    private Long costId;

    @NotNull(message = NOT_BLANK_MESSAGE)
    private Boolean exceedAllowedLimit;

    private String explanation;

    public JustificationForm() {
    }

    public JustificationForm(Justification justification) {
        this.costId = justification.getId();
        this.exceedAllowedLimit = justification.getExceedAllowedLimit();
        this.explanation = justification.getExplanation();
    }

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
    }

    public Boolean getExceedAllowedLimit() {
        return exceedAllowedLimit;
    }

    public void setExceedAllowedLimit(Boolean exceedAllowedLimit) {
        this.exceedAllowedLimit = exceedAllowedLimit;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
