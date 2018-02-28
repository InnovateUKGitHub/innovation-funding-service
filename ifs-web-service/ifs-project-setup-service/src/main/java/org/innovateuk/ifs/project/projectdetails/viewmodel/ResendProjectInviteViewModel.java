package org.innovateuk.ifs.project.projectdetails.viewmodel;

// View model for the resend project invite non-javascript confirmation page

import java.util.Objects;

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
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(organisationId, that.organisationId) &&
                Objects.equals(inviteId, that.inviteId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(projectId, organisationId, inviteId);
    }
}
