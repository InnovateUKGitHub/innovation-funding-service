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

    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="roleId", referencedColumnName="id")
    private Role role;

    private Long organisationId;

    public ProcessRole(){
    	// no-arg constructor
    }

    public ProcessRole(Long id, User user, Long applicationId, Role role, Long organisationId) {
        this.id = id;
        this.user = user;
        this.applicationId = applicationId;
        this.role = role;
        this.organisationId = organisationId;
    }

    public ProcessRole(User user, Long applicationId, Role role, Long organisationId) {
        this.user = user;
        this.applicationId = applicationId;
        this.role = role;
        this.organisationId = organisationId;
    }

    public Role getRole() {
        return role;
    }

    public User getUser() {
        return user;
    }
    @JsonIgnore
    public Long getApplication() {
        return applicationId;
    }

    public Long getOrganisation() {
        return organisationId;
    }

    public Long getId() {
        return id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setApplication(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setOrganisation(Long organisationId) {
        this.organisationId = organisationId;
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
