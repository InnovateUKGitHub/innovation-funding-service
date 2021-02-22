package org.innovateuk.ifs.questionnaire.resource;

public class QuestionnaireTextOutcomeResource extends QuestionnaireDecisionResource {

    private String text;

    private QuestionnaireDecisionImplementation implementation;

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
