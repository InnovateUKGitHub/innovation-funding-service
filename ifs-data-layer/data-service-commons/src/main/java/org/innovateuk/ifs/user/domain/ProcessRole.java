package org.innovateuk.ifs.user.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleType;

import javax.persistence.*;

/**
 * ProcessRole defines database relations and a model to use client side and server side.
 */
@Entity
public class ProcessRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="userId", referencedColumnName="id", nullable = false)
    private User user;

    private long applicationId;

    @Column(name = "role_id")
    private ProcessRoleType role;

    private Long organisationId;

    public ProcessRole(){
    }

    public ProcessRole(User user, long applicationId, ProcessRoleType role, long organisationId) {
        this.user = user;
        this.applicationId = applicationId;
        this.role = role;
        this.organisationId = organisationId;
    }

    public ProcessRole(User user, long applicationId, ProcessRoleType role) {
        this.user = user;
        this.applicationId = applicationId;
        this.role = role;
        this.organisationId = null;
    }

    public ProcessRoleType getRole() {
        return role;
    }

    public User getUser() {
        return user;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getId() {
        return id;
    }

    public void setRole(ProcessRoleType role) {
        this.role = role;
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

    public boolean isKta() {
        return role.isKta();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProcessRole that = (ProcessRole) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(user == null ? null : user.getId(), that.user == null ? null : that.user.getId())
                .append(applicationId, that.applicationId)
                .append(role, that.role)
                .append(organisationId, that.organisationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        Long userId;
        if (user == null) {
            userId = null;
        } else {
            userId = user.getId();
        }
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(userId)
                .append(applicationId)
                .append(role)
                .append(organisationId)
                .toHashCode();
    }
}