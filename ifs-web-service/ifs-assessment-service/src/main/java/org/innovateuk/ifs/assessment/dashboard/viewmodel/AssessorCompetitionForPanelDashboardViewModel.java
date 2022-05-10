package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition for Panel Dashboard.
 */
@Getter
public class AssessorCompetitionForPanelDashboardViewModel {

    private long competitionId;
    private String competitionTitle;
    private String leadTechnologist;
    private ZonedDateTime panelDate;
    private List<AssessorCompetitionForPanelDashboardApplicationViewModel> applications;
    private String hash;

    public AssessorCompetitionForPanelDashboardViewModel(long competitionId,
                                                         String competitionTitle,
                                                         String leadTechnologist,
                                                         ZonedDateTime panelDate,
                                                         List<AssessorCompetitionForPanelDashboardApplicationViewModel> applications,
                                                         String hash) {
        this.competitionId = competitionId;
        this.competitionTitle = competitionTitle;
        this.leadTechnologist = leadTechnologist;
        this.panelDate = panelDate;
        this.applications = applications;
        this.hash = hash;
    }

}
