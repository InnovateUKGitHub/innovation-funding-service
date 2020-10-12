package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DTO for competition invite statistics
 */
public class CompetitionInviteStatisticsResource {

    private int invited;
    private int accepted;
    private int declined;
    private int inviteList;

    public int getInvited() {
        return invited;
    }

    public void setInvited(int invited) {
        this.invited = invited;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public int getDeclined() {
        return declined;
    }

    public void setDeclined(int declined) {
        this.declined = declined;
    }

    public int getInviteList() {
        return inviteList;
    }

    public void setInviteList(int inviteList) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("invited", invited)
                .append("accepted", accepted)
                .append("declined", declined)
                .append("inviteList", inviteList)
                .toString();
    }
}
