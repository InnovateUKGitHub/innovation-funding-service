package org.innovateuk.ifs.review.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for assessment panel invite statistics
 */
public class ReviewInviteStatisticsResource {
    private int invited;
    private int accepted;
    private int declined;

    public ReviewInviteStatisticsResource() {
    }

    public ReviewInviteStatisticsResource(int invited, int accepted, int declined) {
        this.invited = invited;
        this.accepted = accepted;
        this.declined = declined;
    }

    public int getInvited() {
        return invited;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getDeclined() {
        return declined;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ReviewInviteStatisticsResource that = (ReviewInviteStatisticsResource) o;

        return new EqualsBuilder()
                .append(invited, that.invited)
                .append(accepted, that.accepted)
                .append(declined, that.declined)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(invited)
                .append(accepted)
                .append(declined)
                .toHashCode();
    }
}
