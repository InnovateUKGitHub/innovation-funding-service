package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * Resource for group of new users to be sent invites.
 */
public class NewUserStagedInviteListResource {

    @Valid
    private List<NewUserStagedInviteResource> invites;

    public NewUserStagedInviteListResource() {}

    public NewUserStagedInviteListResource(List<NewUserStagedInviteResource> invites) {
        this.invites = invites;
    }

    public List<NewUserStagedInviteResource> getInvites() {
        return invites;
    }

    public void setInvites(List<NewUserStagedInviteResource> invites) {
        this.invites = invites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NewUserStagedInviteListResource that = (NewUserStagedInviteListResource) o;

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
