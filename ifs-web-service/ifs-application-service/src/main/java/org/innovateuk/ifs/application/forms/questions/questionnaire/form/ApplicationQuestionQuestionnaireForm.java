package org.innovateuk.ifs.application.forms.questions.questionnaire.form;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

public class ApplicationQuestionQuestionnaireForm {

    @NotNull(message = "{validation.application.terms.accept.required}")
    @AssertTrue(message = "{validation.application.terms.accept.required}")
    private Boolean agreement;

    public Boolean getAgreement() {
        return agreement;
    }

    public void setAgreement(Boolean agreement) {
        this.agreement = agreement;
    }
}
