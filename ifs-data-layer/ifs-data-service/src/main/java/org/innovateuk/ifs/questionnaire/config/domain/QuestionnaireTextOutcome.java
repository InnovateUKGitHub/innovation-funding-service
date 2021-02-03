package org.innovateuk.ifs.questionnaire.config.domain;

import javax.persistence.*;

@Entity
public class QuestionnaireTextOutcome extends QuestionnaireDecision {

    private String text;

    @Enumerated(EnumType.STRING)
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
