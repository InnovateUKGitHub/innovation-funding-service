package org.innovateuk.ifs.project.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.domain.InvitedParticipant;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * ProjectUser defines a User's role on a Project and in relation to a particular Organisation.
 */
@Entity
@DiscriminatorValue("PROJECT_USER")
public class ProjectUser extends ProjectParticipant implements InvitedParticipant<Project, ProjectUserInvite, ProjectParticipantRole> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationId", referencedColumnName = "id")
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id", referencedColumnName = "id")
    private ProjectUserInvite invite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", referencedColumnName = "id")
    private Project project;

    public ProjectUser() {
    }

    public ProjectUser(User user, Project project, ProjectParticipantRole role, Organisation organisation) {
        super(user, role);
        if (!ProjectParticipantRole.PROJECT_USER_ROLES.contains(role)) {
            throw new IllegalArgumentException("ProjectUser cannot have role " + role);
        }
        this.organisation = organisation;
        this.project = project;
    }

    @Override
    public Project getProcess() {
        return project;
    }

    @Override
    public ProjectUserInvite getInvite() {
        return invite;
    }

    public ProjectUser accept() {
        if (getInvite().getStatus() != OPENED) {
            throw new IllegalStateException("Cannot accept a ProjectUser that hasn't been opened");
        }
        if (getStatus() == REJECTED) {
            throw new IllegalStateException("Cannot accept a ProjectUser that has been rejected");
        }
        if (getStatus() == ACCEPTED) {
            throw new IllegalStateException("ProjectUser has already been accepted");
        }

        setStatus(ACCEPTED);

        return this;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public boolean isUser(Long userId) {
        return getUser().hasId(userId);
    }

    public boolean isPartner() {
        return getRole().isPartner();
    }

    public boolean isFinanceContact() {
        return getRole().isFinanceContact();
    }

    public boolean isProjectManager() {
        return getRole().isProjectManager();
    }

    public void setInvite(ProjectUserInvite invite) {
        this.invite = invite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectUser that = (ProjectUser) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(organisation, that.organisation)
                .append(invite, that.invite)
                .append(project, that.project)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(organisation)
                .append(invite)
                .append(project)
                .toHashCode();
    }
}