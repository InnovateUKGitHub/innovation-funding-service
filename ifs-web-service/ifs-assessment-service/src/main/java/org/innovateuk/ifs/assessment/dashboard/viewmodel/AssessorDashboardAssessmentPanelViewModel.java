package org.innovateuk.ifs.assessment.dashboard.viewmodel;

/**
 * Abstract view model for sharing attributes that are part of panel information
 */
public abstract class AssessorDashboardAssessmentPanelViewModel {
    private String competitionName;
    private long competitionId;

    protected AssessorDashboardAssessmentPanelViewModel(String competitionName, long competitionId) {
        this.competitionName = competitionName;
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public long getCompetitionId() {
        return competitionId;
    }
}
