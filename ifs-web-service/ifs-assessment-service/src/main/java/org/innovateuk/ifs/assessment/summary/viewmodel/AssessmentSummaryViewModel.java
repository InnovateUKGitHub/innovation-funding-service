package org.innovateuk.ifs.assessment.summary.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Holder of model attributes for the Assessment Summary displayed when a review is requested by the Assessor.
 */
public class AssessmentSummaryViewModel {

    private final long assessmentId;
    private final long applicationId;
    private final String applicationName;
    private final long daysLeft;
    private final long daysLeftPercentage;
    private final List<AssessmentSummaryQuestionViewModel> questionsForReview;
    private final int totalScoreGiven;
    private final int totalScorePossible;
    private final int totalScorePercentage;

    public AssessmentSummaryViewModel(final AssessmentResource assessment,
                                      final CompetitionResource competition,
                                      final List<AssessmentSummaryQuestionViewModel> questionViewModels) {
        this.assessmentId = assessment.getId();
        this.applicationId = assessment.getApplication();
        this.applicationName = assessment.getApplicationName();
        this.daysLeft = competition.getAssessmentDaysLeft();
        this.daysLeftPercentage = competition.getAssessmentDaysLeftPercentage();
        this.questionsForReview = questionViewModels;

        this.totalScoreGiven = getTotalScoreGiven(questionViewModels);
        this.totalScorePossible = getTotalScorePossible(questionViewModels);
        this.totalScorePercentage = totalScorePossible == 0 ? 0 : Math.round(totalScoreGiven * 100.0f / totalScorePossible);
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

    private static int getTotalScoreGiven(List<AssessmentSummaryQuestionViewModel> questions) {
        return questions.stream()
                .filter(AssessmentSummaryQuestionViewModel::isScoreFormInputExists)
                .mapToInt(question -> ofNullable(question.getScoreGiven()).orElse(0))
                .sum();
    }

    private static int getTotalScorePossible(List<AssessmentSummaryQuestionViewModel> questions) {
        return questions.stream()
                .filter(AssessmentSummaryQuestionViewModel::isScoreFormInputExists)
                .mapToInt(question -> ofNullable(question.getScorePossible()).orElse(0))
                .sum();
    }
}
