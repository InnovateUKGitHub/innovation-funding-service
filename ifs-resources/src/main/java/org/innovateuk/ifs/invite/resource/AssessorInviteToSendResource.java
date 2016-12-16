package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.email.resource.EmailContent;

/**
 * Resource for invites to be sent
 */
public class AssessorInviteToSendResource {
    private String recipient;
    private long competitionId;
    private String competitionName;
    private EmailContent emailContent;

    public AssessorInviteToSendResource() {

    }

    public AssessorInviteToSendResource(String recipient, long competitionId, String competitionName, EmailContent emailContent) {
        this.recipient = recipient;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.emailContent = emailContent;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public EmailContent getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(EmailContent emailContent) {
        this.emailContent = emailContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorInviteToSendResource that = (AssessorInviteToSendResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(recipient, that.recipient)
                .append(competitionName, that.competitionName)
                .append(emailContent, that.emailContent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(recipient)
                .append(competitionId)
                .append(competitionName)
                .append(emailContent)
                .toHashCode();
    }
}
