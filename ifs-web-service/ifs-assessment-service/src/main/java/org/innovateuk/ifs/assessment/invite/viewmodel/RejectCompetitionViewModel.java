package org.innovateuk.ifs.assessment.invite.viewmodel;

/**
 * Holder of model attributes for the Reject Competition view.
 */
public class RejectCompetitionViewModel {

    private String competitionInviteHash;
    private String competitionName;

    public RejectCompetitionViewModel(String competitionInviteHash, String competitionName) {
        this.competitionInviteHash = competitionInviteHash;
        this.competitionName = competitionName;
    }

    public String getCompetitionInviteHash() {
        return competitionInviteHash;
    }

    public void setCompetitionInviteHash(String competitionInviteHash) {
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }
}
