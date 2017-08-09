package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType;

/**
 * Holder of model attributes for the Application Progress view.
 */
public class AssessorAssessmentProgressRemoveViewModel {

    private long competitionId;
    private long assessorId;
    private long assessmentId;
    private AvailableAssessorsSortFieldType sortField;

    public AssessorAssessmentProgressRemoveViewModel(
            long competitionId,
            long assessorId,
            long assessmentId,
            AvailableAssessorsSortFieldType sortField) {
        this.competitionId = competitionId;
        this.assessorId = assessorId;
        this.assessmentId = assessmentId;
        this.sortField = sortField;
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

    public AvailableAssessorsSortFieldType getSortField() {
        return sortField;
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
                .append(sortField, that.sortField)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(assessorId)
                .append(assessmentId)
                .append(sortField)
                .toHashCode();
    }
}