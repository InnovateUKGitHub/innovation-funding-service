package org.innovateuk.ifs.assessment.dashboard.viewmodel;

public class AssessorDashboardInterviewViewModel {
    private String competitionName;
    private long competitionId;

    protected AssessorDashboardInterviewViewModel(String competitionName, long competitionId) {
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