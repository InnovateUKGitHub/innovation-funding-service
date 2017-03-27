package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource for invites to be sent
 */
public class AssessorInviteToSendResource {
    private String recipient;
    private long competitionId;
    private String competitionName;
    private String content;

    public AssessorInviteToSendResource() {

    }

    public AssessorInviteToSendResource(String recipient, long competitionId, String competitionName, String content) {
        this.recipient = recipient;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorInviteToSendResource that = (AssessorInviteToSendResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(recipient, that.recipient)
                .append(competitionName, that.competitionName)
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(recipient)
                .append(competitionId)
                .append(competitionName)
                .append(content)
                .toHashCode();
    }
}
