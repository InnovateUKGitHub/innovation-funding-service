package com.worth.ifs.domain;

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

    public List<UserApplicationRole> getUserApplicationRoles() {
        return userApplicationRoles;
    }

}
