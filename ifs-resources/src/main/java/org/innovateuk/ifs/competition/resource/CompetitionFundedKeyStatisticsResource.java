package org.innovateuk.ifs.competition.resource;

/**
 * Key stats to be displayed in the competitions funded panel
 */
public class CompetitionFundedKeyStatisticsResource {

    private int applicationsSubmitted;
    private int applicationsFunded;
    private int applicationsNotFunded;
    private int applicationsOnHold;
    private int applicationsNotifiedOfDecision;
    private int applicationsAwaitingDecision;

    public int getApplicationsSubmitted() {
        return applicationsSubmitted;
    }

    public void setApplicationsSubmitted(int applicationsSubmitted) {
        this.applicationsSubmitted = applicationsSubmitted;
    }

    public int getApplicationsFunded() {
        return applicationsFunded;
    }

    public void setApplicationsFunded(int applicationsFunded) {
        this.applicationsFunded = applicationsFunded;
    }

    public int getApplicationsNotFunded() {
        return applicationsNotFunded;
    }

    public void setApplicationsNotFunded(int applicationsNotFunded) {
        this.applicationsNotFunded = applicationsNotFunded;
    }

    public int getApplicationsOnHold() {
        return applicationsOnHold;
    }

    public void setApplicationsOnHold(int applicationsOnHold) {
        this.applicationsOnHold = applicationsOnHold;
    }

    public int getApplicationsNotifiedOfDecision() {
        return applicationsNotifiedOfDecision;
    }

    public void setApplicationsNotifiedOfDecision(int applicationsNotifiedOfDecision) {
        this.applicationsNotifiedOfDecision = applicationsNotifiedOfDecision;
    }

    public int getApplicationsAwaitingDecision() {
        return applicationsAwaitingDecision;
    }

    public void setApplicationsAwaitingDecision(int applicationsAwaitingDecision) {
        this.applicationsAwaitingDecision = applicationsAwaitingDecision;
    }

    public boolean isCanManageFundingNotifications() {
        return applicationsFunded > 0 || applicationsNotFunded > 0 || applicationsOnHold > 0;
    }

    public boolean isCanReleaseFeedback() {
        return applicationsAwaitingDecision == 0 && applicationsSubmitted == applicationsNotifiedOfDecision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompetitionFundedKeyStatisticsResource that = (CompetitionFundedKeyStatisticsResource) o;

        if (applicationsSubmitted != that.applicationsSubmitted) return false;
        if (applicationsFunded != that.applicationsFunded) return false;
        if (applicationsNotFunded != that.applicationsNotFunded) return false;
        if (applicationsOnHold != that.applicationsOnHold) return false;
        if (applicationsNotifiedOfDecision != that.applicationsNotifiedOfDecision) return false;
        return applicationsAwaitingDecision == that.applicationsAwaitingDecision;

    }

    @Override
    public int hashCode() {
        int result = applicationsSubmitted;
        result = 31 * result + applicationsFunded;
        result = 31 * result + applicationsNotFunded;
        result = 31 * result + applicationsOnHold;
        result = 31 * result + applicationsNotifiedOfDecision;
        result = 31 * result + applicationsAwaitingDecision;
        return result;
    }
}
