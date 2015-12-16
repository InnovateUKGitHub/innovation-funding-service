package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    private List<User> users = new ArrayList<>();

    public Role() {
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

    public void setName(String name) {
        this.name = name;
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
        Role rhs = (Role) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.name, rhs.name)
            .append(this.processRoles, rhs.processRoles)
            .append(this.users, rhs.users)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(name)
            .append(processRoles)
            .append(users)
            .toHashCode();
    }
}
