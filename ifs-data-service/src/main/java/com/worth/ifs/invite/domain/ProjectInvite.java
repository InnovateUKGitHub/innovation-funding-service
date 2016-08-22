package com.worth.ifs.invite.domain;

import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("PROJECT")
public class ProjectInvite extends Invite<Project, ProjectInvite> {

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Project project;

    public ProjectInvite() {
        // no-arg constructor
    }

    public ProjectInvite(final String name, final String email, final String hash, final Organisation organisation, final Project project, final InviteStatus status) {
        super(name, email, hash, status);
        this.project = project;
        this.organisation = organisation;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(final Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public Project getTarget() {
        return project;
    }

    @Override
    public void setTarget(final Project project) {
        this.project = project;
    }
}
