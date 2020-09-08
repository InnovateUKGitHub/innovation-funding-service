package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import javax.validation.constraints.NotNull;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.NOT_BLANK_MESSAGE;

public class JustificationForm {

    @NotNull(message = NOT_BLANK_MESSAGE)
    private Boolean exceedAllowedLimit;

    private String explanation;

    public JustificationForm() {
    }

    public JustificationForm(Boolean exceedAllowedLimit, String explanation) {
        this.exceedAllowedLimit = exceedAllowedLimit;
        this.explanation = explanation;
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
