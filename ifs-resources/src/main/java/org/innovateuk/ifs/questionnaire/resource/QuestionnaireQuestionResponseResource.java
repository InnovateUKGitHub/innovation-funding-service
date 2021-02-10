package org.innovateuk.ifs.questionnaire.resource;

public class QuestionnaireQuestionResponseResource {

    private Long id;

    private Long option;

    private Long questionnaireResponse;

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

    public Long getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(Long questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }
}
