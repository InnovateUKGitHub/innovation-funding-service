package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Role {
    public Role(long id, String name, List<UserApplicationRole> userApplicationRoles) {
        this.id = id;
        this.name = name;
        this.userApplicationRoles = userApplicationRoles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    @OneToMany(mappedBy = "role")
    private List<UserApplicationRole> userApplicationRoles = new ArrayList<UserApplicationRole>();

    public Role() {
    }

    protected boolean canEqual(Object other) {
        return other instanceof Role;
    }


    public List<UserApplicationRole> getUserApplicationRoles() {
        return userApplicationRoles;
    }

    public void setUserApplicationRoles(List<UserApplicationRole> userApplicationRoles) {
        this.userApplicationRoles = userApplicationRoles;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
