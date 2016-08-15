package com.worth.ifs.invite.domain;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Optional;

/**
 * A {@link Participant} in a Project.
 */
@Entity
@Table(name="project_user")
public class ProjectParticipant extends Participant<Project, ProjectInvite, ProjectParticipantRole> {

    @ManyToOne(optional = false)
    @Column(name = "project_id")
    private Project project;

    @ManyToOne(optional = false)
    @Column(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne(optional = true)
    @Column(name = "user_id")
    private User user;

    // role_id
    // TODO what is this referencing?


    // TODO we don't have a reference to invite on project_user
    // private ProjectInvite;

    @Override
    public Project getProcess() {
        return project;
    }

    @Override
    public Optional<ProjectInvite> getInvite() {
        // TODO
//        return Optional.of(invite);
        return Optional.of(null);
    }

    @Override
    public ProjectParticipantRole getRole() {
        // TODO
        return null;
    }

    @Override
    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }
}
