package com.worth.ifs.assessment.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Assessor Dashboard.
 */
public class AssessorDashboardViewModel {

    private List<AssessorDashboardUpcomingCompetitionViewModel> invitations;
    private List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions;
    private List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions;

    public AssessorDashboardViewModel(List<AssessorDashboardUpcomingCompetitionViewModel> invitations, List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions, List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions) {
        this.invitations = invitations;
        this.activeCompetitions = activeCompetitions;
        this.upcomingCompetitions = upcomingCompetitions;
    }

    public List<AssessorDashboardUpcomingCompetitionViewModel> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<AssessorDashboardUpcomingCompetitionViewModel> invitations) {
        this.invitations = invitations;
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