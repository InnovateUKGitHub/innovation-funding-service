package org.innovateuk.ifs.assessment.invite.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Form field model for the panel rejection content
 */
public class PanelInviteForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.panelinvitedecision.required}")
    private Boolean acceptInvitation;

    public Boolean getAcceptInvitation() {
        return acceptInvitation;
    }

    public void setAcceptInvitation(Boolean acceptInvitation) {
        this.acceptInvitation = acceptInvitation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PanelInviteForm that = (PanelInviteForm) o;

        return new EqualsBuilder()
                .append(acceptInvitation, that.acceptInvitation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(acceptInvitation)
                .toHashCode();
    }
}
