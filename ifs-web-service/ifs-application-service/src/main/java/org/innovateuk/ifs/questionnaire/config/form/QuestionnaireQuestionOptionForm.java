package org.innovateuk.ifs.questionnaire.config.form;

import org.innovateuk.ifs.questionnaire.resource.DecisionType;

public class QuestionnaireQuestionOptionForm {

    private Long optionId;
    private String text;
    private Long decisionId;
    private DecisionType decisionType;
    private String questionTitle;
    private String textOutcome;

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(Long decisionId) {
        this.decisionId = decisionId;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(DecisionType decisionType) {
        this.decisionType = decisionType;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getTextOutcome() {
        return textOutcome;
    }

    public void setTextOutcome(String textOutcome) {
        this.textOutcome = textOutcome;
    }

    public boolean isQuestionDecision() {
        return decisionType == DecisionType.QUESTION;
    }

    public boolean isTextOutcomeDecision() {
        return decisionType == DecisionType.TEXT_OUTCOME;
    }
}
