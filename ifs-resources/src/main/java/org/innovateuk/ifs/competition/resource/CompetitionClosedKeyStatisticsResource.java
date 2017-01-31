package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionClosedKeyStatisticsResource {
    private long assessorsInvited;
    private long assessorsAccepted;
    private long applicationsPerAssessor;
    private long applicationsRequiringAssessors;
    private long assessorsWithoutApplications;
    private long assignmentCount;

    public long getAssessorsInvited() {
        return assessorsInvited;
    }

    public void setAssessorsInvited(long assessorsInvited) {
        this.assessorsInvited = assessorsInvited;
    }

    public long getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public void setAssessorsAccepted(long assessorsAccepted) {
        this.assessorsAccepted = assessorsAccepted;
    }

    public long getApplicationsPerAssessor() {
        return applicationsPerAssessor;
    }

    public void setApplicationsPerAssessor(long applicationsPerAssessor) {
        this.applicationsPerAssessor = applicationsPerAssessor;
    }

    public long getApplicationsRequiringAssessors() {
        return applicationsRequiringAssessors;
    }

    public void setApplicationsRequiringAssessors(long applicationsRequiringAssessors) {
        this.applicationsRequiringAssessors = applicationsRequiringAssessors;
    }

    public long getAssessorsWithoutApplications() {
        return assessorsWithoutApplications;
    }

    public void setAssessorsWithoutApplications(long assessorsWithoutApplications) {
        this.assessorsWithoutApplications = assessorsWithoutApplications;
    }

    public long getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(long assignmentCount) {
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
