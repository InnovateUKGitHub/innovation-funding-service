package org.innovateuk.ifs.assessment.summary.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public AssessmentSummaryQuestionViewModel(Long questionId,
                                              String displayLabel,
                                              String displayLabelShort,
                                              boolean scoreFormInputExists,
                                              Integer scoreGiven,
                                              Integer scorePossible,
                                              String feedback,
                                              Boolean applicationInScope,
                                              boolean complete) {
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

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getDisplayLabelShort() {
        return displayLabelShort;
    }

    public boolean isScoreFormInputExists() {
        return scoreFormInputExists;
    }

    public Integer getScoreGiven() {
        return scoreGiven;
    }

    public Integer getScorePossible() {
        return scorePossible;
    }

    public String getFeedback() {
        return feedback;
    }

    public Boolean getApplicationInScope() {
        return applicationInScope;
    }

    public boolean isComplete() {
        return complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentSummaryQuestionViewModel that = (AssessmentSummaryQuestionViewModel) o;

        return new EqualsBuilder()
                .append(scoreFormInputExists, that.scoreFormInputExists)
                .append(complete, that.complete)
                .append(questionId, that.questionId)
                .append(displayLabel, that.displayLabel)
                .append(displayLabelShort, that.displayLabelShort)
                .append(scoreGiven, that.scoreGiven)
                .append(scorePossible, that.scorePossible)
                .append(feedback, that.feedback)
                .append(applicationInScope, that.applicationInScope)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(questionId)
                .append(displayLabel)
                .append(displayLabelShort)
                .append(scoreFormInputExists)
                .append(scoreGiven)
                .append(scorePossible)
                .append(feedback)
                .append(applicationInScope)
                .append(complete)
                .toHashCode();
    }
}
