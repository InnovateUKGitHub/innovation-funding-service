package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProcessRole defines database relations and a model to use client side and server side.
 */
@Entity
public class ProcessRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="userId", referencedColumnName="id")
    private User user;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne
    @JoinColumn(name="roleId", referencedColumnName="id")
    private Role role;

    @ManyToOne
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    @OneToMany(mappedBy="updatedBy",fetch = FetchType.LAZY)
    private List<Response> responses = new ArrayList<>();

    public ProcessRole(){

    }

    public ProcessRole(Long id, User user, Application application, Role role, Organisation organisation) {
        this.id = id;
        this.user = user;
        this.application = application;
        this.role = role;
        this.organisation = organisation;
    }

    public ProcessRole(User user, Application application, Role role, Organisation organisation) {
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
    public Application getApplication() {
        return application;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ProcessRole rhs = (ProcessRole) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.user, rhs.user)
            .append(this.application, rhs.application)
            .append(this.role, rhs.role)
            .append(this.organisation, rhs.organisation)
            .append(this.responses, rhs.responses)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(user)
            .append(application)
            .append(role)
            .append(organisation)
            .append(responses)
            .toHashCode();
    }
}
