package org.innovateuk.ifs.competition.resource;

public class CompetitionReadyToOpenKeyStatisticsResource {
    private long assessorsInvited;
    private long assessorsAccepted;

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
}
