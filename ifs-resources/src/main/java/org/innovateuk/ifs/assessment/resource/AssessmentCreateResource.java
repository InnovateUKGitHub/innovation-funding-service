package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;

/**
 * DTO for creating an Assessment in its initial state.
 */
public class AssessmentCreateResource {

    @NotNull(message = "{validation.assessmentCreate.applicationId.required")
    private long applicationId;
    @NotNull(message = "{validation.assessmentCreate.assessorId.required")
    private Long assessorId;

    public AssessmentCreateResource() {
    }

    public AssessmentCreateResource(Long competitionId, Long assessorId) {
        this.applicationId = competitionId;
        this.assessorId = assessorId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getAssessorId() {
        return assessorId;
    }

    public void setAssessorId(Long assessorId) {
        this.assessorId = assessorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentCreateResource that = (AssessmentCreateResource) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(assessorId, that.assessorId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(assessorId)
                .toHashCode();
    }
}
