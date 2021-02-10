package org.innovateuk.ifs.questionnaire.resource;

import java.util.List;

public class QuestionnaireResponseResource {

    private Long id;

    private Long questionnaire;

    private List<Long> questionnaireQuestionResponse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Long questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<Long> getQuestionnaireQuestionResponse() {
        return questionnaireQuestionResponse;
    }

    public void setQuestionnaireQuestionResponse(List<Long> questionnaireQuestionResponse) {
        this.questionnaireQuestionResponse = questionnaireQuestionResponse;
    }
}
