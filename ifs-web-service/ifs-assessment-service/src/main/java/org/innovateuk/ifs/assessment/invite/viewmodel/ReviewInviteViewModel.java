package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;

import java.time.ZonedDateTime;

/**
 * ViewModel of a ReviewInvite.
 */
public class ReviewInviteViewModel extends BaseInviteViewModel {

    private ZonedDateTime panelDate;

    public ReviewInviteViewModel(String panelInviteHash, ReviewInviteResource invite, boolean userLoggedIn) {
        super(panelInviteHash, invite.getCompetitionId(), invite.getCompetitionName(), userLoggedIn);
        this.panelDate = invite.getPanelDate();
    }

    public String getPanelInviteHash() {
        return getInviteHash();
    }

    public ZonedDateTime getPanelDate() {
        return panelDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ReviewInviteViewModel that = (ReviewInviteViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(panelDate, that.panelDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(panelDate)
                .toHashCode();
    }
}
