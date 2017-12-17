package org.innovateuk.ifs.assessment.dashboard.viewmodel;

/**
 * Holder of model attributes for assessment panel invites on the assessor dashboard.
 */
public class AssessorDashboardAssessmentPanelInviteViewModel extends AssessorDashboardAssessmentPanelViewModel {

    private String inviteHash;

    public AssessorDashboardAssessmentPanelInviteViewModel(String competitionName, long competitionId, String hash) {
        super(competitionName, competitionId);
        this.inviteHash = hash;
    }

    public String getInviteHash() {
        return inviteHash;
    }
}
