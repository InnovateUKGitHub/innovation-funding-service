package org.innovateuk.ifs.application.forms.questions.questionnaire.form;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

public class QuestionQuestionnaireForm {

    @NotNull(message = "{validation.subsidy.basis.agreement.required}")
    @AssertTrue(message = "{validation.subsidy.basis.agreement.required}")
    private Boolean agreement;

    public Boolean getAgreement() {
        return agreement;
    }

    public void setAgreement(Boolean agreement) {
        this.agreement = agreement;
    }
}
