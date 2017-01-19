package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;
import java.util.Set;

/**
 * DTO for a created assessor invite that is ready to be sent.
 */
public class AssessorCreatedInviteResource extends AssessorInviteResource {

    private String email;
    private long inviteId;

    public AssessorCreatedInviteResource() {
    }

    public AssessorCreatedInviteResource(String name, List<InnovationAreaResource> innovationAreas, boolean compliant, String email, long inviteId) {
        super(name, innovationAreas, compliant);
        this.email = email;
        this.inviteId = inviteId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getInviteId() {
        return inviteId;
    }

    public void setInviteId(long inviteId) {
        this.inviteId = inviteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorCreatedInviteResource that = (AssessorCreatedInviteResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(email, that.email)
                .append(inviteId, that.inviteId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(email)
                .append(inviteId)
                .toHashCode();
    }
}