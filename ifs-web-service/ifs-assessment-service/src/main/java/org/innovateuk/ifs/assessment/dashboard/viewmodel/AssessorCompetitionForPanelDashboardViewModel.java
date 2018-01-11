package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition for Panel Dashboard.
 */
public class AssessorCompetitionForPanelDashboardViewModel {

    private long competitionId;
    private String competitionTitle;
    private String leadTechnologist;
    private ZonedDateTime panelDate;
    private List<AssessorCompetitionForPanelDashboardApplicationViewModel> applications;

    public AssessorCompetitionForPanelDashboardViewModel(long competitionId,
                                                         String competitionTitle,
                                                         String leadTechnologist,
                                                         ZonedDateTime panelDate,
                                                         List<AssessorCompetitionForPanelDashboardApplicationViewModel> applications) {
        this.competitionId = competitionId;
        this.competitionTitle = competitionTitle;
        this.leadTechnologist = leadTechnologist;
        this.panelDate = panelDate;
        this.applications = applications;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public String getLeadTechnologist() {
        return leadTechnologist;
    }

    public ZonedDateTime getPanelDate() {
        return panelDate;
    }

    public List<AssessorCompetitionForPanelDashboardApplicationViewModel> getApplications() {
        return applications;
    }
}
