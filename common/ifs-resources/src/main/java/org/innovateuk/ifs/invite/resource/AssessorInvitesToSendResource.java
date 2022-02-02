package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Resource for invites to be sent
 */
public class AssessorInvitesToSendResource {
    private List<String> recipients;
    private long competitionId;
    private String competitionName;
    private String content;

    public AssessorInvitesToSendResource() {

    }

    public AssessorInvitesToSendResource(List<String> recipients, long competitionId, String competitionName, String content) {
        this.recipients = recipients;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.content = content;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
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

        AssessorInvitesToSendResource that = (AssessorInvitesToSendResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(recipients, that.recipients)
                .append(competitionName, that.competitionName)
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(recipients)
                .append(competitionId)
                .append(competitionName)
                .append(content)
                .toHashCode();
    }
}
