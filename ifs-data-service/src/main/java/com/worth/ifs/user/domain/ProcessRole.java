package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;

import javax.persistence.*;
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
    private List<Response> responses;

    public ProcessRole(){

    }

    public ProcessRole(Long id, User user, Application application, Role role, Organisation organisation) {
        this.id = id;
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
}
