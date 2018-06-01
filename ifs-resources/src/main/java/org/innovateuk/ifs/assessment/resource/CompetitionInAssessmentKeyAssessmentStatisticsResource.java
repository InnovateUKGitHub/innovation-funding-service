package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionInAssessmentKeyAssessmentStatisticsResource {

    private int assignmentCount;
    private int assignmentsWaiting;
    private int assignmentsAccepted;
    private int assessmentsStarted;
    private int assessmentsSubmitted;

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(int assignmentCount) {
        this.assignmentCount = assignmentCount;
    }

    public int getAssignmentsWaiting() {
        return assignmentsWaiting;
    }

    public void setAssignmentsWaiting(int assignmentsWaiting) {
        this.assignmentsWaiting = assignmentsWaiting;
    }

    public int getAssignmentsAccepted() {
        return assignmentsAccepted;
    }

    public void setAssignmentsAccepted(int assignmentsAccepted) {
        this.assignmentsAccepted = assignmentsAccepted;
    }

    public int getAssessmentsStarted() {
        return assessmentsStarted;
    }

    public void setAssessmentsStarted(int assessmentsStarted) {
        this.assessmentsStarted = assessmentsStarted;
    }

    public int getAssessmentsSubmitted() {
        return assessmentsSubmitted;
    }

    public void setAssessmentsSubmitted(int assessmentsSubmitted) {
        this.assessmentsSubmitted = assessmentsSubmitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionInAssessmentKeyAssessmentStatisticsResource that = (CompetitionInAssessmentKeyAssessmentStatisticsResource) o;

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
