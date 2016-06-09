package com.worth.ifs.project.domain;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;

import javax.persistence.*;

/**
 * ProjectUser defines a User's role on a Project and in relation to a particular Organisation.
 */
@Entity
public class ProjectUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="userId", referencedColumnName="id")
    private User user;

    @ManyToOne
    @JoinColumn(name="projectId", referencedColumnName="id")
    private Project project;

    @ManyToOne
    @JoinColumn(name="roleId", referencedColumnName="id")
    private Role role;

    @ManyToOne
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    public ProjectUser(){
    	// no-arg constructor
    }

    public ProjectUser(Long id, User user, Project project, Role role, Organisation organisation) {
        this.id = id;
        this.user = user;
        this.project = project;
        this.role = role;
        this.organisation = organisation;
    }

    public ProjectUser(User user, Project project, Role role, Organisation organisation) {
        this.user = user;
        this.project = project;
        this.role = role;
        this.organisation = organisation;
    }

    public Role getRole() {
        return role;
    }

    public User getUser() {
        return user;
    }

    public Project getProject() {
        return project;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public Long getId() {
        return id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPartner() {
        return getRole().isPartner();
    }
}
