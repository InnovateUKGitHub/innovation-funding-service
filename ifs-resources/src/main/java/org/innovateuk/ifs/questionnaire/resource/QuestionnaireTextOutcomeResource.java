package org.innovateuk.ifs.questionnaire.resource;

public class QuestionnaireTextOutcomeResource {

    private Long id;
    private String text;

    private QuestionnaireDecisionImplementation implementation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionnaireDecisionImplementation getImplementation() {
        return implementation;
    }

    public void setImplementation(QuestionnaireDecisionImplementation implementation) {
        this.implementation = implementation;
    }
}
