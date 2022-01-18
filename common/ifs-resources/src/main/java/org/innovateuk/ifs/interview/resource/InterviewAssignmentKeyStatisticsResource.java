package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for interview assignment key statistics
 */
public class InterviewAssignmentKeyStatisticsResource {

    private int applicationsInCompetition;
    private int applicationsAssigned;

    public InterviewAssignmentKeyStatisticsResource() {
    }

    public InterviewAssignmentKeyStatisticsResource(int applicationsInCompetition, int applicationsAssigned) {
        this.applicationsInCompetition = applicationsInCompetition;
        this.applicationsAssigned = applicationsAssigned;
    }

    public int getApplicationsInCompetition() {
        return applicationsInCompetition;
    }

    public int getApplicationsAssigned() {
        return applicationsAssigned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignmentKeyStatisticsResource that = (InterviewAssignmentKeyStatisticsResource) o;

        return new EqualsBuilder()
                .append(applicationsInCompetition, that.applicationsInCompetition)
                .append(applicationsAssigned, that.applicationsAssigned)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationsInCompetition)
                .append(applicationsAssigned)
                .toHashCode();
    }
}