package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionClosedKeyStatisticsResource {
    private int assessorsInvited;
    private int assessorsAccepted;
    private int applicationsPerAssessor;
    private int applicationsRequiringAssessors;
    private int assessorsWithoutApplications;
    private int assignmentCount;

    public int getAssessorsInvited() {
        return assessorsInvited;
    }

    public void setAssessorsInvited(int assessorsInvited) {
        this.assessorsInvited = assessorsInvited;
    }

    public int getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public void setAssessorsAccepted(int assessorsAccepted) {
        this.assessorsAccepted = assessorsAccepted;
    }

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

    public int getAssessorsWithoutApplications() {
        return assessorsWithoutApplications;
    }

    public void setAssessorsWithoutApplications(int assessorsWithoutApplications) {
        this.assessorsWithoutApplications = assessorsWithoutApplications;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(int assignmentCount) {
        this.assignmentCount = assignmentCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionClosedKeyStatisticsResource that = (CompetitionClosedKeyStatisticsResource) o;

        return new EqualsBuilder()
                .append(assessorsInvited, that.assessorsInvited)
                .append(assessorsAccepted, that.assessorsAccepted)
                .append(applicationsPerAssessor, that.applicationsPerAssessor)
                .append(applicationsRequiringAssessors, that.applicationsRequiringAssessors)
                .append(assessorsWithoutApplications, that.assessorsWithoutApplications)
                .append(assignmentCount, that.assignmentCount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessorsInvited)
                .append(assessorsAccepted)
                .append(applicationsPerAssessor)
                .append(applicationsRequiringAssessors)
                .append(assessorsWithoutApplications)
                .append(assignmentCount)
                .toHashCode();
    }
}
