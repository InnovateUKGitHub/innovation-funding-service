package org.innovateuk.ifs.application.forms.questions.questionnaire.form;

import javax.validation.constraints.NotNull;

public class ApplicationQuestionQuestionnaireForm {

    @NotNull
    private Boolean agreement;

    public Boolean getAgreement() {
        return agreement;
    }

    public void setAgreement(Boolean agreement) {
        this.agreement = agreement;
    }
}
