package org.innovateuk.ifs.competition.resource;

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
}
