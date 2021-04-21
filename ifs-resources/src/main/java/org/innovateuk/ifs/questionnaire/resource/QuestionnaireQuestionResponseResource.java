package org.innovateuk.ifs.questionnaire.resource;

public class QuestionnaireQuestionResponseResource {

    private Long id;

    private Long option;

    private String questionnaireResponse;

    private Long question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOption() {
        return option;
    }

    public void setOption(Long option) {
        this.option = option;
    }

    public String getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(String questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }
}
