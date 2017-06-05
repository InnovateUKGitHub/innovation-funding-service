package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Send Invite view.
 */
public class SendInviteViewModel {

    private long competitionId;
    private long inviteId;
    private String competitionName;
    private String recipient;
    private String content;

    public SendInviteViewModel(long competitionId, long inviteId, String competitionName, String recipient, String content) {
        this.competitionId = competitionId;
        this.inviteId = inviteId;
        this.competitionName = competitionName;
        this.recipient = recipient;
        this.content = content;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getInviteId() {
        return inviteId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SendInviteViewModel that = (SendInviteViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(inviteId, that.inviteId)
                .append(competitionName, that.competitionName)
                .append(recipient, that.recipient)
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(inviteId)
                .append(competitionName)
                .append(recipient)
                .append(content)
                .toHashCode();
    }
}
