package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Key stats to be displayed in the competitions funded panel
 */
public class CompetitionFundedKeyApplicationStatisticsResource {

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

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CompetitionFundedKeyApplicationStatisticsResource that = (CompetitionFundedKeyApplicationStatisticsResource) o;

        return new EqualsBuilder()
                .append(applicationsSubmitted, that.applicationsSubmitted)
                .append(applicationsFunded, that.applicationsFunded)
                .append(applicationsNotFunded, that.applicationsNotFunded)
                .append(applicationsOnHold, that.applicationsOnHold)
                .append(applicationsNotifiedOfDecision, that.applicationsNotifiedOfDecision)
                .append(applicationsAwaitingDecision, that.applicationsAwaitingDecision)
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .append(applicationsSubmitted)
                .append(applicationsFunded)
                .append(applicationsNotFunded)
                .append(applicationsOnHold)
                .append(applicationsNotifiedOfDecision)
                .append(applicationsAwaitingDecision)
                .toHashCode();
    }
}
