package org.innovateuk.ifs.questionnaire.viewmodel;

public class PreviousQuestionViewModel {

    private final long questionnaireQuestionId;
    private final String question;
    private final String answer;

    public PreviousQuestionViewModel(long questionnaireQuestionId, String question, String answer) {
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
