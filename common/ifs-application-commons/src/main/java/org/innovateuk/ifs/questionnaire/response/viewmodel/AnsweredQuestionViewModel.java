package org.innovateuk.ifs.questionnaire.response.viewmodel;

public class AnsweredQuestionViewModel {

    private final long questionnaireQuestionId;
    private final String question;
    private final String questionName;
    private final String answer;

    public AnsweredQuestionViewModel(long questionnaireQuestionId, String question, String questionName, String answer) {
        this.questionnaireQuestionId = questionnaireQuestionId;
        this.question = question;
        this.questionName = questionName;
        this.answer = answer;
    }

    public long getQuestionnaireQuestionId() {
        return questionnaireQuestionId;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestionName() {
        return questionName;
    }

    public String getAnswer() {
        return answer;
    }
}
