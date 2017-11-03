package org.innovateuk.ifs.assessment.invite.viewmodel;

/**
 * Holder of model attributes for the Reject Invite view.
 */
public class RejectInviteViewModel {

    private String inviteHash;
    private String competitionName;

    public RejectInviteViewModel(String inviteHash, String competitionName) {
        this.inviteHash = inviteHash;
        this.competitionName = competitionName;
    }

    public String getInviteHash() {
        return inviteHash;
    }

    public void setCompetitionInviteHash(String inviteHash) {
        this.inviteHash = inviteHash;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }
}
