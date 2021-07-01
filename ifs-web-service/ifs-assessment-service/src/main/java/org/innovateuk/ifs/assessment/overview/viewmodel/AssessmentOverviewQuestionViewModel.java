package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Holder of model attributes for questions displayed within the Assessment Overview view.
 */
public class AssessmentOverviewQuestionViewModel {

    private long questionId;
    private String questionName;
    private String questionNumber;
    private Integer maximumScore;
    private boolean responseRequired;
    private boolean assessed;
    private Boolean scopeResponse;
    private String scoreResponse;
    private boolean scoreRequired;

    public AssessmentOverviewQuestionViewModel(long questionId,
                                               String questionName,
                                               String questionNumber,
                                               Integer maximumScore,
                                               boolean responseRequired,
                                               boolean assessed,
                                               Boolean scopeResponse,
                                               String scoreResponse,
                                               boolean scoreRequired) {
        this.questionId = questionId;
        this.questionName = questionName;
        this.questionNumber = questionNumber;
        this.maximumScore = maximumScore;
        this.responseRequired = responseRequired;
        this.assessed = assessed;
        this.scopeResponse = scopeResponse;
        this.scoreResponse = scoreResponse;
        this.scoreRequired = scoreRequired;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Integer getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(Integer maximumScore) {
        this.maximumScore = maximumScore;
    }

    public boolean isResponseRequired() {
        return responseRequired;
    }

    public void setResponseRequired(boolean responseRequired) {
        this.responseRequired = responseRequired;
    }

    public boolean isAssessed() {
        return assessed;
    }

    public void setAssessed(boolean assessed) {
        this.assessed = assessed;
    }

    public Boolean getScopeResponse() {
        return scopeResponse;
    }

    public void setScopeResponse(Boolean scopeResponse) {
        this.scopeResponse = scopeResponse;
    }

    public String getScoreResponse() {
        return scoreResponse;
    }

    public void setScoreResponse(String scoreResponse) {
        this.scoreResponse = scoreResponse;
    }

    public boolean isScoreRequired() {
        return scoreRequired;
    }

    public void setScoreRequired(boolean scoreRequired) {
        this.scoreRequired = scoreRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentOverviewQuestionViewModel that = (AssessmentOverviewQuestionViewModel) o;

        return new EqualsBuilder()
                .append(questionId, that.questionId)
                .append(responseRequired, that.responseRequired)
                .append(assessed, that.assessed)
                .append(questionName, that.questionName)
                .append(questionNumber, that.questionNumber)
                .append(maximumScore, that.maximumScore)
                .append(scopeResponse, that.scopeResponse)
                .append(scoreResponse, that.scoreResponse)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(questionId)
                .append(questionName)
                .append(questionNumber)
                .append(maximumScore)
                .append(responseRequired)
                .append(assessed)
                .append(scopeResponse)
                .append(scoreResponse)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("questionId", questionId)
                .append("questionName", questionName)
                .append("questionNumber", questionNumber)
                .append("maximumScore", maximumScore)
                .append("responseRequired", responseRequired)
                .append("assessed", assessed)
                .append("scopeResponse", scopeResponse)
                .append("scoreResponse", scoreResponse)
                .append("scoreRequired", scoreRequired)
                .toString();
    }
}