package org.innovateuk.ifs.questionnaire.response.viewmodel;

public class AnsweredQuestionViewModel {

    private final long questionnaireResponseId;
    private final long questionnaireQuestionId;
    private final String question;
    private final String answer;

    public AnsweredQuestionViewModel(long questionnaireResponseId, long questionnaireQuestionId, String question, String answer) {
        this.questionnaireResponseId = questionnaireResponseId;
        this.questionnaireQuestionId = questionnaireQuestionId;
        this.question = question;
        this.answer = answer;
    }

    public long getQuestionnaireResponseId() {
        return questionnaireResponseId;
    }

    public long getQuestionnaireQuestionId() {
        return questionnaireQuestionId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
