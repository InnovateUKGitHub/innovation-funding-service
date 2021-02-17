package org.innovateuk.ifs.questionnaire.resource;

import java.util.List;

public class QuestionnaireResponseResource {

    private String uid;

    private Long questionnaire;

    private List<Long> questionnaireQuestionResponse;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
