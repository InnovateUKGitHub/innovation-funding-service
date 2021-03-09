package org.innovateuk.ifs.questionnaire.response.viewmodel;

import java.util.List;

public class AnswerTableViewModel {

    private final String questionnaireResponseId;
    private final String title;
    private final List<AnsweredQuestionViewModel> questions;
    private final boolean readonly;

    public AnswerTableViewModel(String questionnaireResponseId, String title, List<AnsweredQuestionViewModel> questions, boolean readonly) {
        this.questionnaireResponseId = questionnaireResponseId;
        this.title = title;
        this.questions = questions;
        this.readonly = readonly;
    }

    public String getQuestionnaireResponseId() {
        return questionnaireResponseId;
    }

    public String getTitle() {
        return title;
    }

    public List<AnsweredQuestionViewModel> getQuestions() {
        return questions;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public String getLink() {
        return String.format("/questionnaire/%s", questionnaireResponseId);
    }

    public String getLink(long questionId) {
        return String.format("/questionnaire/%s/question/%d", questionnaireResponseId, questionId);
    }
}
