package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import javax.validation.constraints.NotNull;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.NOT_BLANK_MESSAGE;

public class JustificationForm {

    @NotNull(message = NOT_BLANK_MESSAGE)
    private Boolean exceedAllowedLimit;

    private String justification;

    public JustificationForm() {
    }

    public JustificationForm(Boolean exceedAllowedLimit, String justification) {
        this.exceedAllowedLimit = exceedAllowedLimit;
        this.justification = justification;
    }

    public Boolean getExceedAllowedLimit() {
        return exceedAllowedLimit;
    }

    public void setExceedAllowedLimit(Boolean exceedAllowedLimit) {
        this.exceedAllowedLimit = exceedAllowedLimit;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
