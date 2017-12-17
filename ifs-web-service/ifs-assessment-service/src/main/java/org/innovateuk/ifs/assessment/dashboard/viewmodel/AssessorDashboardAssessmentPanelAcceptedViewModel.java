package org.innovateuk.ifs.assessment.dashboard.viewmodel;

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
}
