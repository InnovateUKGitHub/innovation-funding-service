package org.innovateuk.ifs.questionnaire.response.viewmodel;

public class AnsweredQuestionViewModel {

    private final String questionnaireResponseId;
    private final long questionnaireQuestionId;
    private final String question;
    private final String answer;

    public AnsweredQuestionViewModel(String questionnaireResponseId, long questionnaireQuestionId, String question, String answer) {
        this.questionnaireResponseId = questionnaireResponseId;
        this.questionnaireQuestionId = questionnaireQuestionId;
        this.question = question;
        this.answer = answer;
    }

    public String getQuestionnaireResponseId() {
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
