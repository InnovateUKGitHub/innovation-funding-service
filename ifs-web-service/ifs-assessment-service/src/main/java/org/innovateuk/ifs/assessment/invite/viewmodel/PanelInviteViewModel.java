package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;

import java.time.ZonedDateTime;

/**
 * ViewModel of an AssessmentPanelInvite.
 */
public class PanelInviteViewModel extends BaseInviteViewModel {

    private ZonedDateTime panelDate;

    public PanelInviteViewModel(String panelInviteHash, AssessmentPanelInviteResource invite, boolean userLoggedIn) {
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

        PanelInviteViewModel that = (PanelInviteViewModel) o;

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
