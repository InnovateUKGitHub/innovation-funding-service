package org.innovateuk.ifs.assessment.panel.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for assessment panel invite statistics
 */
public class AssessmentPanelInviteStatisticsResource {
    private int invited;
    private int accepted;
    private int declined;
    private int pending;

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

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentPanelInviteStatisticsResource that = (AssessmentPanelInviteStatisticsResource) o;

        return new EqualsBuilder()
                .append(invited, that.invited)
                .append(accepted, that.accepted)
                .append(declined, that.declined)
                .append(pending, that.pending)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(invited)
                .append(accepted)
                .append(declined)
                .append(pending)
                .toHashCode();
    }
}
