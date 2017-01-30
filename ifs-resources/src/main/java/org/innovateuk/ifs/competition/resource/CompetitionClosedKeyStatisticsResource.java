package org.innovateuk.ifs.competition.resource;

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
}
