package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * Resource for group of existing assessors to be sent invites.
 */
public class ExistingUserStagedInviteListResource {
    @Valid
    private List<ExistingUserStagedInviteResource> invites;

    public ExistingUserStagedInviteListResource() {}

    public ExistingUserStagedInviteListResource(List<ExistingUserStagedInviteResource> invites) {
        this.invites = invites;
    }

    public List<ExistingUserStagedInviteResource> getInvites() {
        return invites;
    }

    public void setInvites(List<ExistingUserStagedInviteResource> invites) {
        this.invites = invites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExistingUserStagedInviteListResource that = (ExistingUserStagedInviteListResource) o;

        return new EqualsBuilder()
                .append(invites, that.invites)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(invites)
                .toHashCode();
    }
}
