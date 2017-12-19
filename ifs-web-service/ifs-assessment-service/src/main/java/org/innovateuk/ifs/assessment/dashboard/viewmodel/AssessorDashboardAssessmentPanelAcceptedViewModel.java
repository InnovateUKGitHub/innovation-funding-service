package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

public class AssessorDashboardAssessmentPanelAcceptedViewModel extends AssessorDashboardAssessmentPanelViewModel{

    private LocalDate panelDateDeadline;
    private long daysLeft;
    private long awaitingApplications;

    public AssessorDashboardAssessmentPanelAcceptedViewModel(String competitionName, long competitionId, LocalDate panelDateDeadline, long daysLeft, long awaitingApplications) {
        super(competitionName, competitionId);
        this.panelDateDeadline = panelDateDeadline;
        this.daysLeft = daysLeft;
        this.awaitingApplications = awaitingApplications;
    }

    public LocalDate getPanelDateDeadline() {
        return panelDateDeadline;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getAwaitingApplications() {
        return awaitingApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorDashboardAssessmentPanelAcceptedViewModel that = (AssessorDashboardAssessmentPanelAcceptedViewModel) o;

        return new EqualsBuilder()
                .append(daysLeft, that.daysLeft)
                .append(awaitingApplications, that.awaitingApplications)
                .append(panelDateDeadline, that.panelDateDeadline)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(panelDateDeadline)
                .append(daysLeft)
                .append(awaitingApplications)
                .toHashCode();
    }
}
