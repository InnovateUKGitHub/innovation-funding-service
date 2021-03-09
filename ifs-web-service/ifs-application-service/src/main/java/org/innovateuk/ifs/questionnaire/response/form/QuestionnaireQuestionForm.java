package org.innovateuk.ifs.questionnaire.response.form;

import javax.validation.constraints.NotNull;

public class QuestionnaireQuestionForm {

    @NotNull(message = "{validation.questionnaire.answer.required}")
    private Long option;
    private Long questionResponseId;

    public Long getOption() {
        return option;
    }

    public void setOption(Long option) {
        this.option = option;
    }

    public Long getQuestionResponseId() {
        return questionResponseId;
    }

    public void setQuestionResponseId(Long questionResponseId) {
        this.questionResponseId = questionResponseId;
    }
}
