package com.worth.ifs.assessment.viewmodel;

/**
 * Holder of model attributes for the questions shown as part of the Assessment Summary.
 */
public class AssessmentSummaryQuestionViewModel {

    private Long questionId;
    private String displayLabel;
    private String displayLabelShort;
    private boolean scoreFormInputExists;
    private Integer scoreGiven;
    private Integer scorePossible;
    private String feedback;
    private Boolean applicationInScope;
    private boolean complete;

    public AssessmentSummaryQuestionViewModel(Long questionId, String displayLabel, String displayLabelShort, boolean scoreFormInputExists, Integer scoreGiven, Integer scorePossible, String feedback, Boolean applicationInScope, boolean complete) {
        this.questionId = questionId;
        this.displayLabel = displayLabel;
        this.displayLabelShort = displayLabelShort;
        this.scoreFormInputExists = scoreFormInputExists;
        this.scoreGiven = scoreGiven;
        this.scorePossible = scorePossible;
        this.feedback = feedback;
        this.applicationInScope = applicationInScope;
        this.complete = complete;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabelShort() {
        return displayLabelShort;
    }

    public void setDisplayLabelShort(String displayLabelShort) {
        this.displayLabelShort = displayLabelShort;
    }

    public boolean isScoreFormInputExists() {
        return scoreFormInputExists;
    }

    public void setScoreFormInputExists(boolean scoreFormInputExists) {
        this.scoreFormInputExists = scoreFormInputExists;
    }

    public Integer getScoreGiven() {
        return scoreGiven;
    }

    public void setScoreGiven(Integer scoreGiven) {
        this.scoreGiven = scoreGiven;
    }

    public Integer getScorePossible() {
        return scorePossible;
    }

    public void setScorePossible(Integer scorePossible) {
        this.scorePossible = scorePossible;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Boolean getApplicationInScope() {
        return applicationInScope;
    }

    public void setApplicationInScope(Boolean applicationInScope) {
        this.applicationInScope = applicationInScope;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}