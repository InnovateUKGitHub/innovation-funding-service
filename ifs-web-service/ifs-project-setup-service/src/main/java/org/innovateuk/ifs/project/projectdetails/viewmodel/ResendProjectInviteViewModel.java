package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

/**
 * View model for the resend project invite non-javascript confirmation page
 */

public class ResendProjectInviteViewModel {

    private Long projectId;
    private Long organisationId;
    private Long inviteId;

    public ResendProjectInviteViewModel(Long projectId, Long inviteId, Long organisationId) {
        this.projectId = projectId;
        this.inviteId = inviteId;
        this.organisationId = organisationId;
    }

    public ResendProjectInviteViewModel(Long projectId, Long inviteId) {
        this.projectId = projectId;
        this.inviteId = inviteId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getInviteId() {
        return inviteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ResendProjectInviteViewModel that = (ResendProjectInviteViewModel) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(organisationId, that.organisationId)
                .append(inviteId, that.inviteId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(organisationId)
                .append(inviteId)
                .toHashCode();
    }
}
