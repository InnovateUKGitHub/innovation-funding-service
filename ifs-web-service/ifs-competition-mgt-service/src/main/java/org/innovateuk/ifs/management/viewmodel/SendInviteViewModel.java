package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the Send Invites view.
 */
public class SendInviteViewModel {
    private Long competitionId;
    private String competitionName;
    private String recipient;

    public SendInviteViewModel(Long competitionId, String competitionName, String recipient) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.recipient = recipient;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
