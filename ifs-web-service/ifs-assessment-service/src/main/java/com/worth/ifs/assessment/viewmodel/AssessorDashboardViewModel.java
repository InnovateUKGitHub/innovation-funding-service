package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileStatusViewModel;

import java.util.List;

/**
 * Holder of model attributes for the Assessor Dashboard.
 */
public class AssessorDashboardViewModel {

    private List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions;
    private List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions;
    private AssessorProfileStatusViewModel profileStatus;


    public AssessorDashboardViewModel(AssessorProfileStatusViewModel profileStatus, List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions, List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions) {
        this.profileStatus = profileStatus;
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

    public AssessorProfileStatusViewModel getProfileStatus() {
        return profileStatus;
    }
}