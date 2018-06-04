package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionClosedKeyApplicationStatisticsResource {

    private int applicationsPerAssessor;
    private int applicationsRequiringAssessors;
    private int assignmentCount;

    public int getApplicationsPerAssessor() {
        return applicationsPerAssessor;
    }

    public void setApplicationsPerAssessor(int applicationsPerAssessor) {
        this.applicationsPerAssessor = applicationsPerAssessor;
    }

    public int getApplicationsRequiringAssessors() {
        return applicationsRequiringAssessors;
    }

    public void setApplicationsRequiringAssessors(int applicationsRequiringAssessors) {
        this.applicationsRequiringAssessors = applicationsRequiringAssessors;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(int assignmentCount) {
        this.assignmentCount = assignmentCount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CompetitionClosedKeyApplicationStatisticsResource that =
                (CompetitionClosedKeyApplicationStatisticsResource) o;

        return new EqualsBuilder()
                .append(applicationsPerAssessor, that.applicationsPerAssessor)
                .append(applicationsRequiringAssessors, that.applicationsRequiringAssessors)
                .append(assignmentCount, that.assignmentCount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationsPerAssessor)
                .append(applicationsRequiringAssessors)
                .append(assignmentCount)
                .toHashCode();
    }
}
