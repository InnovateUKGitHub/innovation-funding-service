package org.innovateuk.ifs.covid.form;

import javax.validation.constraints.NotNull;

public class CovidQuestionaireForm {

    @NotNull(message = "{validation.covid.questionnaire.required}")
    private Boolean answer;

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
