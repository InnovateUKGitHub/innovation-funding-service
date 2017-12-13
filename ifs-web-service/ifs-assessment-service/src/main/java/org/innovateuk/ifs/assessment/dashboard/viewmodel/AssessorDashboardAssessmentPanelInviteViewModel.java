package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.tomcat.jni.Local;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Holder of model attributes for assessment panel invites on the assessor dashboard.
 */
public class AssessorDashboardAssessmentPanelInviteViewModel {

    private final String hash;
    private final String competitionName;
    private final long competitionId;
    private final LocalDate panelDateDeadline;
    private long daysLeft;
    private final long awaitingApplications;

    public AssessorDashboardAssessmentPanelInviteViewModel(
            String hash,
            String competitionName,
            long competitionId,
            LocalDate panelDateDeadline,
            long daysLeft,
            long awaitingApplications
            ) {
        this.hash = hash;
        this.competitionName = competitionName;
        this.competitionId = competitionId;
        this.panelDateDeadline = panelDateDeadline;
        this.daysLeft = daysLeft;
        this.awaitingApplications = awaitingApplications;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getInviteHash() {
        return hash;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public LocalDate getPanelDateDeadline() {
        return panelDateDeadline;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(long daysLeft) {
        this.daysLeft = daysLeft;
    }

    public long getAwaitingApplications() {
        return awaitingApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorDashboardAssessmentPanelInviteViewModel that = (AssessorDashboardAssessmentPanelInviteViewModel) o;

        return new EqualsBuilder()
                .append(hash, that.hash)
                .append(competitionName, that.competitionName)
                .append(competitionId, that.competitionId)
                .append(panelDateDeadline, that.panelDateDeadline)
                .append(daysLeft, that.daysLeft)
                .append(awaitingApplications, that.awaitingApplications)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hash)
                .append(competitionName)
                .append(competitionId)
                .append(panelDateDeadline)
                .append(daysLeft)
                .append(awaitingApplications)
                .toHashCode();
    }
}
