package org.innovateuk.ifs.management.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Form for resending competition invites
 */
public class ResendInviteForm extends SendInviteForm {

    private List<Long> inviteIds;

    public List<Long> getInviteIds() {
        return inviteIds;
    }

    public void setInviteIds(List<Long> inviteIds) {
        this.inviteIds = inviteIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ResendInviteForm that = (ResendInviteForm) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(inviteIds, that.inviteIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(inviteIds)
                .toHashCode();
    }
}
