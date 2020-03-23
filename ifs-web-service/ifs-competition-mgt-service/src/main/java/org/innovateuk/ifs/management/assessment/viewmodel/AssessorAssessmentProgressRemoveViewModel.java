package org.innovateuk.ifs.management.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Assessor Progress view.
 */
public class AssessorAssessmentProgressRemoveViewModel {

    private long competitionId;
    private long assessorId;
    private long assessmentId;

    public AssessorAssessmentProgressRemoveViewModel(
            long competitionId,
            long assessorId,
            long assessmentId) {
        this.competitionId = competitionId;
        this.assessorId = assessorId;
        this.assessmentId = assessmentId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getAssessorId() {
        return assessorId;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorAssessmentProgressRemoveViewModel that = (AssessorAssessmentProgressRemoveViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(assessorId, that.assessorId)
                .append(assessmentId, that.assessmentId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(assessorId)
                .append(assessmentId)
                .toHashCode();
    }
}