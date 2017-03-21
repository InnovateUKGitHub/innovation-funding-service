package org.innovateuk.ifs.user.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getId() {
        return id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setOrganisationId(Long organisationId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProcessRole that = (ProcessRole) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(user, that.user)
                .append(applicationId, that.applicationId)
                .append(role, that.role)
                .append(organisationId, that.organisationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(user)
                .append(applicationId)
                .append(role)
                .append(organisationId)
                .toHashCode();
    }
}
