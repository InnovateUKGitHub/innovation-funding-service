package com.worth.ifs.assessment.viewmodel;

import java.util.Map;

/**
 * Holder of model attributes for the questions shown as part of an Application Summary during assessment.
 */
public class AssessmentApplicationSummaryQuestionViewModel {

    private Long questionId;
    private String displayLabel;
    private String displayLabelShort;
    private boolean hasScore;
    private Integer scorePossible;
    private Map<String, String> values;
    private boolean complete;

    public AssessmentApplicationSummaryQuestionViewModel(Long questionId, String displayLabel, String displayLabelShort, boolean hasScore, Integer scorePossible, Map<String, String> values, boolean complete) {
        this.questionId = questionId;
        this.displayLabel = displayLabel;
        this.displayLabelShort = displayLabelShort;
        this.hasScore = hasScore;
        this.scorePossible = scorePossible;
        this.values = values;
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

    public boolean isHasScore() {
        return hasScore;
    }

    public void setHasScore(boolean hasScore) {
        this.hasScore = hasScore;
    }

    public Integer getScorePossible() {
        return scorePossible;
    }

    public void setScorePossible(Integer scorePossible) {
        this.scorePossible = scorePossible;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
