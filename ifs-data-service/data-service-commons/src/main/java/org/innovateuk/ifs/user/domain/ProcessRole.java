package org.innovateuk.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * ProcessRole defines database relations and a model to use client side and server side.
 */
@Entity
public class ProcessRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId", referencedColumnName="id")
    private User user;

    private Long application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="roleId", referencedColumnName="id")
    private Role role;

    private Long organisation;

    public ProcessRole(){
    	// no-arg constructor
    }

    public ProcessRole(Long id, User user, Long application, Role role, Long organisation) {
        this.id = id;
        this.user = user;
        this.application = application;
        this.role = role;
        this.organisation = organisation;
    }

    public ProcessRole(User user, Long application, Role role, Long organisation) {
        this.user = user;
        this.application = application;
        this.role = role;
        this.organisation = organisation;
    }

    public Role getRole() {
        return role;
    }

    public User getUser() {
        return user;
    }
    @JsonIgnore
    public Long getApplication() {
        return application;
    }

    public Long getOrganisation() {
        return application;
    }

    public Long getId() {
        return id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLeadApplicant() {
        return role.isLeadApplicant();
    }

    public boolean isCollaborator() {
        return role.isCollaborator();
    }

    public boolean isLeadApplicantOrCollaborator() {
        return isLeadApplicant() || isCollaborator();
    }
}
