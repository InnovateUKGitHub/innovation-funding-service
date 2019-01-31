package org.innovateuk.ifs.project.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
@Table(name = "project_user")
public class ProjectUser extends ProjectParticipant<ProjectUserInvite, ProjectUserRole> {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationId", referencedColumnName = "id")
    private Organisation organisation;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role")
    private ProjectUserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id", referencedColumnName = "id")
    private ProjectUserInvite invite;

    public ProjectUser() {
    }

    @Override
    public ProjectUserInvite getInvite() {
        return invite;
    }

    @Override
    public ProjectUserRole getRole() {
        return role;
    }

    public ProjectUser(User user, Project project, ProjectUserRole role, Organisation organisation) {
        super(user, project);
        this.organisation = organisation;
        this.role = role;
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
                .append(role, that.role)
                .append(invite, that.invite)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(organisation)
                .append(role)
                .append(invite)
                .toHashCode();
    }
}