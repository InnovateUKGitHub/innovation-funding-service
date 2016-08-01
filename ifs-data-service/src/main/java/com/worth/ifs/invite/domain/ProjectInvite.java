package com.worth.ifs.invite.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("PROJECT")
public class ProjectInvite extends Invite<Organisation, Project> {

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Project project;

    ProjectInvite() {
        // no-arg constructor
    }

    @Override
    public Organisation getOwner() {
        return organisation;
    }

    @Override
    public void setOwner(final Organisation organisation) {
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
