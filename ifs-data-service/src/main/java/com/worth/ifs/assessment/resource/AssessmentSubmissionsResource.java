package com.worth.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class AssessmentSubmissionsResource {

    @NotEmpty(message = "{validation.assessmentSubmissions.assessmentIds.required}")
    private List<Long> assessmentIds = new ArrayList<>();

    public List<Long> getAssessmentIds() {
        return assessmentIds;
    }

    public void setAssessmentIds(List<Long> assessmentIds) {
        if (assessmentIds == null) {
            return;
        }

        this.assessmentIds = assessmentIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentSubmissionsResource that = (AssessmentSubmissionsResource) o;

        return new EqualsBuilder()
                .append(assessmentIds, that.assessmentIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentIds)
                .toHashCode();
    }
}
