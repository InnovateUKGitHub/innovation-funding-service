package org.innovateuk.ifs.questionnaire.response.viewmodel;

public class AnsweredQuestionViewModel {

    private final long questionnaireQuestionId;
    private final String question;
    private final String answer;

    public AnsweredQuestionViewModel(long questionnaireQuestionId, String question, String answer) {
        this.questionnaireQuestionId = questionnaireQuestionId;
        this.question = question;
        this.answer = answer;
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
