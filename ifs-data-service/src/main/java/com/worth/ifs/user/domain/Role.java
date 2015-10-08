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
    public Role(Long id, String name, List<ProcessRole> processRoles) {
        this.id = id;
        this.name = name;
        this.processRoles = processRoles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "role")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @ManyToMany(mappedBy="roles")
    private List<User> users = new ArrayList<>();;

    public Role() {
    }

    // copy constructor for builder
    Role(Role other) {
        this.id = other.id;
        this.name = other.name;
        this.processRoles = new ArrayList<>(other.processRoles);
        this.users = new ArrayList<>(other.users);
    }

    protected Boolean canEqual(Object other) {
        return other instanceof Role;
    }

    @JsonIgnore
    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }
}
