package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.finance.domain.ApplicationFinance;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy="organisation")
    private List<UserApplicationRole> userApplicationRoles = new ArrayList<UserApplicationRole>();

    @OneToMany(mappedBy="organisation")
    private List<ApplicationFinance> applicationFinances = new ArrayList<ApplicationFinance>();

    public Organisation() {

    }

    public Organisation(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public List<UserApplicationRole> getUserApplicationRoles() {
        return userApplicationRoles;
    }

    @JsonIgnore
    public List<ApplicationFinance> getApplicationFinances() {
        return applicationFinances;
    }

    public void setUserApplicationRoles(List<UserApplicationRole> userApplicationRoles) {
        this.userApplicationRoles = userApplicationRoles;
    }
}
