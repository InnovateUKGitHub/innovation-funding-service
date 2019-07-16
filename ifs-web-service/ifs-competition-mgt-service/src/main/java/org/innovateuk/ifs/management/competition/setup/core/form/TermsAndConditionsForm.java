package org.innovateuk.ifs.management.competition.setup.core.form;

import javax.validation.constraints.NotNull;

public class TermsAndConditionsForm extends CompetitionSetupForm {

    @NotNull(message = "validation.termsandconditionsform.field.required")
    private Long termsAndConditionsId;

    public Long getTermsAndConditionsId() {
        return termsAndConditionsId;
    }

    public void setTermsAndConditionsId(Long termsAndConditionsId) {
        this.termsAndConditionsId = termsAndConditionsId;
    }
}
