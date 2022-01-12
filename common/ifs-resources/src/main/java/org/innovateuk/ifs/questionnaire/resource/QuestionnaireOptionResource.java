package org.innovateuk.ifs.questionnaire.resource;

public class QuestionnaireOptionResource {

    private Long id;

    private String text;

    private Long decision;

    private Long question;

    private DecisionType decisionType;

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

    public Long getDecision() {
        return decision;
    }

    public void setDecision(Long decision) {
        this.decision = decision;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(DecisionType decisionType) {
        this.decisionType = decisionType;
    }
}
