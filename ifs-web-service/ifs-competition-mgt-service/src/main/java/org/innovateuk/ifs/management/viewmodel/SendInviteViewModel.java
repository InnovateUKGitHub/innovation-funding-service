package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.email.resource.EmailContent;

/**
 * Holder of model attributes for the Send Invites view.
 */
public class SendInviteViewModel {
    private Long competitionId;
    private Long inviteId;
    private String competitionName;
    private String recipient;
    private EmailContent emailContent;

    public SendInviteViewModel(Long competitionId, Long inviteId, String competitionName, String recipient, EmailContent emailContent) {
        this.competitionId = competitionId;
        this.inviteId = inviteId;
        this.competitionName = competitionName;
        this.recipient = recipient;
        this.emailContent = emailContent;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getInviteId() {
        return inviteId;
    }

    public void setInviteId(Long inviteId) {
        this.inviteId = inviteId;
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

    public EmailContent getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(EmailContent emailContent) {
        this.emailContent = emailContent;
    }
}
