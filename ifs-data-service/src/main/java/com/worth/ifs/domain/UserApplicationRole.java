package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * UserApplicationRole defines database relations and a model to use client side and server side.
 */

@Entity
public class UserApplicationRole {
    public UserApplicationRole(Long id, User user, Application application, Role role) {
        this.id = id;
        this.user = user;
        this.application = application;
        this.role = role;
    }

    public UserApplicationRole(){

    }

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

    @OneToMany(mappedBy="userApplicationRole",fetch = FetchType.LAZY)
    private List<Response> responses;

    public Long getRoleId() {
        return role.getId();
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getApplicationId() {
        return application.getId();
    }

    @JsonIgnore
    public Role getRole() {
        return role;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }
    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public List<Response> getResponses() {
        return responses;
    }
}
