package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Application Detail view shown as part of the assessment journey.
 */
public class AssessmentFeedbackApplicationDetailsViewModel {

    private final long daysLeft;
    private final long daysLeftPercentage;
    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final String questionShortName;

    public AssessmentFeedbackApplicationDetailsViewModel(final long daysLeft, final long daysLeftPercentage, final CompetitionResource competition, final ApplicationResource application, final String questionShortName) {
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.competition = competition;
        this.application = application;
        this.questionShortName = questionShortName;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public String getQuestionShortName() {
        return questionShortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentFeedbackApplicationDetailsViewModel that = (AssessmentFeedbackApplicationDetailsViewModel) o;

        return new EqualsBuilder()
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(competition, that.competition)
                .append(application, that.application)
                .append(questionShortName, that.questionShortName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .append(competition)
                .append(application)
                .append(questionShortName)
                .toHashCode();
    }
}
