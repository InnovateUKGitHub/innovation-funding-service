package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Role defines database relations and a model to use client side and server side.
 */

@Entity
public class Role {
    public Role(Long id, String name, List<UserApplicationRole> userApplicationRoles) {
        this.id = id;
        this.name = name;
        this.userApplicationRoles = userApplicationRoles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "role")
    private List<UserApplicationRole> userApplicationRoles = new ArrayList<UserApplicationRole>();

    public Role() {
    }

    protected Boolean canEqual(Object other) {
        return other instanceof Role;
    }

    @JsonIgnore
    public List<UserApplicationRole> getUserApplicationRoles() {
        return userApplicationRoles;
    }

    public void setUserApplicationRoles(List<UserApplicationRole> userApplicationRoles) {
        this.userApplicationRoles = userApplicationRoles;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
