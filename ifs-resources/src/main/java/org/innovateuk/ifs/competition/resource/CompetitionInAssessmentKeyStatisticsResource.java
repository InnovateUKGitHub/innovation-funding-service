package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionInAssessmentKeyStatisticsResource {

    private long assignmentCount;
    private long assignmentsWaiting;
    private long assignmentsAccepted;
    private long assessmentsStarted;
    private long assessmentsSubmitted;

    public long getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(long assignmentCount) {
        this.assignmentCount = assignmentCount;
    }

    public long getAssignmentsWaiting() {
        return assignmentsWaiting;
    }

    public void setAssignmentsWaiting(long assignmentsWaiting) {
        this.assignmentsWaiting = assignmentsWaiting;
    }

    public long getAssignmentsAccepted() {
        return assignmentsAccepted;
    }

    public void setAssignmentsAccepted(long assignmentsAccepted) {
        this.assignmentsAccepted = assignmentsAccepted;
    }

    public long getAssessmentsStarted() {
        return assessmentsStarted;
    }

    public void setAssessmentsStarted(long assessmentsStarted) {
        this.assessmentsStarted = assessmentsStarted;
    }

    public long getAssessmentsSubmitted() {
        return assessmentsSubmitted;
    }

    public void setAssessmentsSubmitted(long assessmentsSubmitted) {
        this.assessmentsSubmitted = assessmentsSubmitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionInAssessmentKeyStatisticsResource that = (CompetitionInAssessmentKeyStatisticsResource) o;

        return new EqualsBuilder()
                .append(assignmentCount, that.assignmentCount)
                .append(assignmentsWaiting, that.assignmentsWaiting)
                .append(assignmentsAccepted, that.assignmentsAccepted)
                .append(assessmentsStarted, that.assessmentsStarted)
                .append(assessmentsSubmitted, that.assessmentsSubmitted)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assignmentCount)
                .append(assignmentsWaiting)
                .append(assignmentsAccepted)
                .append(assessmentsStarted)
                .append(assessmentsSubmitted)
                .toHashCode();
    }
}
