package org.innovateuk.ifs.management.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType;

/**
 * Holder of model attributes for the Application Progress view.
 */
public class ApplicationAssessmentProgressRemoveViewModel {

    private long competitionId;
    private long applicationId;
    private long assessmentId;
    private long assessmentPeriodId;
    private AvailableAssessorsSortFieldType sortField;

    public ApplicationAssessmentProgressRemoveViewModel(
            long competitionId,
            long applicationId,
            long assessmentId,
            long assessmentPeriodId,
            AvailableAssessorsSortFieldType sortField) {
        this.competitionId = competitionId;
        this.applicationId = applicationId;
        this.assessmentId = assessmentId;
        this.assessmentPeriodId = assessmentPeriodId;
        this.sortField = sortField;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public AvailableAssessorsSortFieldType getSortField() {
        return sortField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAssessmentProgressRemoveViewModel that = (ApplicationAssessmentProgressRemoveViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(applicationId, that.applicationId)
                .append(assessmentId, that.assessmentId)
                .append(assessmentPeriodId, that.assessmentPeriodId)
                .append(sortField, that.sortField)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(applicationId)
                .append(assessmentId)
                .append(assessmentPeriodId)
                .append(sortField)
                .toHashCode();
    }
}