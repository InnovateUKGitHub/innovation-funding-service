package org.innovateuk.ifs.assessment.summary.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Holder of model attributes for the Assessment Summary displayed when a review is requested by the Assessor.
 */
public class AssessmentSummaryViewModel {

    private long assessmentId;
    private long applicationId;
    private String applicationName;
    private long daysLeft;
    private long daysLeftPercentage;
    private List<AssessmentSummaryQuestionViewModel> questionsForReview;
    private int totalScoreGiven;
    private int totalScorePossible;
    private int totalScorePercentage;

    public AssessmentSummaryViewModel(long assessmentId,
                                      long applicationId,
                                      String applicationName,
                                      long daysLeft,
                                      long daysLeftPercentage,
                                      List<AssessmentSummaryQuestionViewModel> questionsForReview,
                                      int totalScoreGiven,
                                      int totalScorePossible,
                                      int totalScorePercentage) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.questionsForReview = questionsForReview;
        this.totalScoreGiven = totalScoreGiven;
        this.totalScorePossible = totalScorePossible;
        this.totalScorePercentage = totalScorePercentage;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public List<AssessmentSummaryQuestionViewModel> getQuestionsForReview() {
        return questionsForReview;
    }

    public int getTotalScoreGiven() {
        return totalScoreGiven;
    }

    public int getTotalScorePossible() {
        return totalScorePossible;
    }

    public int getTotalScorePercentage() {
        return totalScorePercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentSummaryViewModel that = (AssessmentSummaryViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(applicationId, that.applicationId)
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(totalScoreGiven, that.totalScoreGiven)
                .append(totalScorePossible, that.totalScorePossible)
                .append(totalScorePercentage, that.totalScorePercentage)
                .append(applicationName, that.applicationName)
                .append(questionsForReview, that.questionsForReview)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .append(questionsForReview)
                .append(totalScoreGiven)
                .append(totalScorePossible)
                .append(totalScorePercentage)
                .toHashCode();
    }
}
