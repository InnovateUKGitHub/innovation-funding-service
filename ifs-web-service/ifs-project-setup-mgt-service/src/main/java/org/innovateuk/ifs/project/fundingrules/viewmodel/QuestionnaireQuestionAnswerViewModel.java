package org.innovateuk.ifs.project.fundingrules.viewmodel;

public class QuestionnaireQuestionAnswerViewModel {
    private String question;
    private String answer;

    public QuestionnaireQuestionAnswerViewModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
