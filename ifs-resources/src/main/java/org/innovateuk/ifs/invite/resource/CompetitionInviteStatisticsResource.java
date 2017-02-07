package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for competition invite statistics
 */
public class CompetitionInviteStatisticsResource {

    private long invited;
    private long accepted;
    private long declined;
    private long inviteList;

    public long getInvited() {
        return invited;
    }

    public void setInvited(long invited) {
        this.invited = invited;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getDeclined() {
        return declined;
    }

    public void setDeclined(long declined) {
        this.declined = declined;
    }

    public long getInviteList() {
        return inviteList;
    }

    public void setInviteList(long inviteList) {
        this.inviteList = inviteList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionInviteStatisticsResource that = (CompetitionInviteStatisticsResource) o;

        return new EqualsBuilder()
                .append(invited, that.invited)
                .append(accepted, that.accepted)
                .append(declined, that.declined)
                .append(inviteList, that.inviteList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(invited)
                .append(accepted)
                .append(declined)
                .append(inviteList)
                .toHashCode();
    }
}
