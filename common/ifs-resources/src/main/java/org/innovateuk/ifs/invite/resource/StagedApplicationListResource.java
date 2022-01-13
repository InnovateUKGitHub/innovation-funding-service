package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * Resource for group of existing applications to be sent invites.
 */
public class StagedApplicationListResource {
    @Valid
    private List<StagedApplicationResource> invites;

    public StagedApplicationListResource() {}

    public StagedApplicationListResource(List<StagedApplicationResource> invites) {
        this.invites = invites;
    }

    public List<StagedApplicationResource> getInvites() {
        return invites;
    }

    public void setInvites(List<StagedApplicationResource> invites) {
        this.invites = invites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StagedApplicationListResource that = (StagedApplicationListResource) o;

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
