package org.innovateuk.ifs.management.assessment.viewmodel;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType;

/**
 * Holder of model attributes for the Application Progress view.
 */
@Getter
public class ApplicationAssessmentProgressUnsubmitViewModel {

    private long competitionId;
    private long applicationId;
    private long assessmentId;
    private long assessmentPeriodId;
    private AvailableAssessorsSortFieldType sortField;

    public ApplicationAssessmentProgressUnsubmitViewModel(
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationAssessmentProgressUnsubmitViewModel that = (ApplicationAssessmentProgressUnsubmitViewModel) o;

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