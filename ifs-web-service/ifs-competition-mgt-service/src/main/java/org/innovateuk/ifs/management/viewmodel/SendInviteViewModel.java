package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Send Invites view.
 */
public class SendInviteViewModel {
    private long competitionId;
    private long inviteId;
    private String competitionName;
    private String recipient;

    public SendInviteViewModel(long competitionId, long inviteId, String competitionName, String recipient) {
        this.competitionId = competitionId;
        this.inviteId = inviteId;
        this.competitionName = competitionName;
        this.recipient = recipient;
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
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(inviteId)
                .append(competitionName)
                .append(recipient)
                .toHashCode();
    }
}
