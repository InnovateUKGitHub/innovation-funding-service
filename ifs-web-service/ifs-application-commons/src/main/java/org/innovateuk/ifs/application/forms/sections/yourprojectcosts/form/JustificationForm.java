package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.commons.validation.constraints.WordCount;

import javax.validation.constraints.NotNull;

public class JustificationForm {

    @NotNull(message = "{validation.ktp.project.costs.exceeded.required}")
    private Boolean exceedAllowedLimit;

    @WordCount(max = 750, message = "{validation.ktp.project.costs.justification.word.count.too.long}")
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
