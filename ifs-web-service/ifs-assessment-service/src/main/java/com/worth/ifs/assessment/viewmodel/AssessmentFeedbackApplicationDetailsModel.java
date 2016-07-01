package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Application Detail view shown as part of the assessment journey.
 */
public class AssessmentFeedbackApplicationDetailsModel {

    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final QuestionResource question;

    public AssessmentFeedbackApplicationDetailsModel(final CompetitionResource competition, final ApplicationResource application, final QuestionResource question) {
        this.competition = competition;
        this.application = application;
        this.question = question;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public QuestionResource getQuestion() {
        return question;
    }

    public long getDaysLeftPercentage() {
        return competition.getAssessmentDaysLeftPercentage();
    }

    public long getDaysLeft() {
        return competition.getAssessmentDaysLeft();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentFeedbackApplicationDetailsModel that = (AssessmentFeedbackApplicationDetailsModel) o;

        return new EqualsBuilder()
                .append(competition, that.competition)
                .append(application, that.application)
                .append(question, that.question)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competition)
                .append(application)
                .append(question)
                .toHashCode();
    }
}
