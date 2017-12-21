package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition Dashboard.
 */
public class AssessorCompetitionForPanelDashboardViewModel {

    private long competitionId;
    private String competitionTitle;
    private String leadTechnologist;
    private ZonedDateTime panelDate;
    private List<AssessorCompetitionDashboardApplicationViewModel> submitted;
    private List<AssessorCompetitionDashboardApplicationViewModel> outstanding;
    private boolean submitVisible;

    public AssessorCompetitionForPanelDashboardViewModel(long competitionId, String competitionTitle, String leadTechnologist, ZonedDateTime panelDate, List<AssessorCompetitionDashboardApplicationViewModel> submitted, List<AssessorCompetitionDashboardApplicationViewModel> outstanding, boolean submitVisible) {
        this.competitionId = competitionId;
        this.competitionTitle = competitionTitle;
        this.leadTechnologist = leadTechnologist;
        this.panelDate = panelDate;
        this.submitted = submitted;
        this.outstanding = outstanding;
        this.submitVisible = submitVisible;
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

    public List<AssessorCompetitionDashboardApplicationViewModel> getSubmitted() {
        return submitted;
    }

    public List<AssessorCompetitionDashboardApplicationViewModel> getOutstanding() {
        return outstanding;
    }

    public boolean isSubmitVisible() {
        return submitVisible;
    }
}
