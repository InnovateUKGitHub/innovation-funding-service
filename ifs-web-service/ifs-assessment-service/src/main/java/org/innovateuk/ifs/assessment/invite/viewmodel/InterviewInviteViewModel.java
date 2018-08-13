package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.resource.InterviewInviteResource;

/**
 * ViewModel of a InterviewInvite.
 */
public class InterviewInviteViewModel  extends BaseInviteViewModel {

    public InterviewInviteViewModel(String panelInviteHash, InterviewInviteResource invite, boolean userLoggedIn) {
        super(panelInviteHash, invite.getCompetitionId(), invite.getCompetitionName(), userLoggedIn);
    }

    public String getPanelInviteHash() {
        return getInviteHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .toHashCode();
    }
}
