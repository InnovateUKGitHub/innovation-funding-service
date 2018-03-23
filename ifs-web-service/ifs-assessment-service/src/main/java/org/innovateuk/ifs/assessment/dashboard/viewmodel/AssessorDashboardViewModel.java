package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileStatusViewModel;

import java.util.List;

/**
 * Holder of model attributes for the Assessor Dashboard.
 */
public class AssessorDashboardViewModel {

    private List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions;
    private List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions;
    private List<AssessorDashboardPendingInviteViewModel> pendingInvites;
    private List<AssessorDashboardAssessmentPanelInviteViewModel> assessmentPanelInvites;
    private List<AssessorDashboardAssessmentPanelAcceptedViewModel> assessmentPanelAccepted;
    private List<AssessorDashboardInterviewInviteViewModel> interviewPanelInvites;
    private List<AssessorDashboardInterviewAcceptedViewModel> interviewPanelAccepted;
    private AssessorProfileStatusViewModel profileStatus;

    public AssessorDashboardViewModel(
            AssessorProfileStatusViewModel profileStatus,
            List<AssessorDashboardActiveCompetitionViewModel> activeCompetitions,
            List<AssessorDashboardUpcomingCompetitionViewModel> upcomingCompetitions,
            List<AssessorDashboardPendingInviteViewModel> pendingInvites,
            List<AssessorDashboardAssessmentPanelInviteViewModel> assessmentPanelInvites,
            List<AssessorDashboardAssessmentPanelAcceptedViewModel> assessmentPanelAccepted,
            List<AssessorDashboardInterviewInviteViewModel> interviewPanelInvites,
            List<AssessorDashboardInterviewAcceptedViewModel> interviewPanelAccepted
            ) {
        this.profileStatus = profileStatus;
        this.activeCompetitions = activeCompetitions;
        this.upcomingCompetitions = upcomingCompetitions;
        this.pendingInvites = pendingInvites;
        this.assessmentPanelInvites = assessmentPanelInvites;
        this.assessmentPanelAccepted = assessmentPanelAccepted;
        this.interviewPanelInvites = interviewPanelInvites;
        this.interviewPanelAccepted = interviewPanelAccepted;
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

    public void setAssessmentPanelInvites(List<AssessorDashboardAssessmentPanelInviteViewModel> assessmentPanelInvites) {
        this.assessmentPanelInvites = assessmentPanelInvites;
    }

    public List<AssessorDashboardAssessmentPanelInviteViewModel> getAssessmentPanelInvites() {
        return assessmentPanelInvites;
    }

    public List<AssessorDashboardAssessmentPanelAcceptedViewModel> getAssessmentPanelAccepted() {
        return assessmentPanelAccepted;
    }

    public void setAssessmentPanelAccepted(List<AssessorDashboardAssessmentPanelAcceptedViewModel> assessmentPanelAccepted) {
        this.assessmentPanelAccepted = assessmentPanelAccepted;
    }

    public List<AssessorDashboardInterviewInviteViewModel> getInterviewPanelInvites() {
        return interviewPanelInvites;
    }

    public List<AssessorDashboardInterviewAcceptedViewModel> getInterviewPanelAccepted() {
        return interviewPanelAccepted;
    }

    public void setInterviewPanelAccepted(List<AssessorDashboardInterviewAcceptedViewModel> interviewPanelAccepted) {
        this.interviewPanelAccepted = interviewPanelAccepted;
    }

    public void setInterviewPanelInvites(List<AssessorDashboardInterviewInviteViewModel> interviewPanelInvites) {
        this.interviewPanelInvites = interviewPanelInvites;
    }

    public AssessorProfileStatusViewModel getProfileStatus() {
        return profileStatus;
    }

    public List<AssessorDashboardPendingInviteViewModel> getPendingInvites() {
        return pendingInvites;
    }
}