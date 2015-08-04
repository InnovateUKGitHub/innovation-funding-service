package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class UserApplicationRole {
    public UserApplicationRole(long id, User user, Application application, Role role) {
        this.id = id;
        this.user = user;
        this.application = application;
        this.role = role;
    }

    public UserApplicationRole(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="userId", referencedColumnName="id")
    private User user;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne
    @JoinColumn(name="roleId", referencedColumnName="id")
    private Role role;

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

    public long getId() {
        return id;
    }
}
