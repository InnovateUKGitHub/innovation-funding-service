package org.innovateuk.ifs.competition.resource;

public class CompetitionOpenKeyStatisticsResource {
    private long assessorsInvited;
    private long assessorsAccepted;
    private long applicationsPerAssessor;
    private long applicationsStarted;
    private long applicationsPastHalf;
    private long applicationsSubmitted;

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

    public long getApplicationsStarted() {
        return applicationsStarted;
    }

    public void setApplicationsStarted(long applicationsStarted) {
        this.applicationsStarted = applicationsStarted;
    }

    public long getApplicationsPastHalf() {
        return applicationsPastHalf;
    }

    public void setApplicationsPastHalf(long applicationsPastHalf) {
        this.applicationsPastHalf = applicationsPastHalf;
    }

    public long getApplicationsSubmitted() {
        return applicationsSubmitted;
    }

    public void setApplicationsSubmitted(long applicationsSubmitted) {
        this.applicationsSubmitted = applicationsSubmitted;
    }
}
