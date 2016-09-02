package com.worth.ifs.assessment.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Assessor Dashboard.
 */
public class AssessorDashboardViewModel {

    private List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions;
    private List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions;

    public AssessorDashboardViewModel(List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions, List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions) {
        this.activeCompetitions = activeCompetitions;
        this.upcomingCompetitions = upcomingCompetitions;
    }

    public List<AssessorDashboardActiveCompetitionViewModel> getActiveCompetitions() {
        return activeCompetitions;
    }

    public void setActiveCompetitions(List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions) {
        this.activeCompetitions = activeCompetitions;
    }

    public List<AssessorDashboardUpcomingCompetitionViewModel> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public void setUpcomingCompetitions(List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions) {
        this.upcomingCompetitions = upcomingCompetitions;
    }
}