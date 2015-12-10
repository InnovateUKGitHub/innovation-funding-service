package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.finance.domain.ApplicationFinance;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Organisation defines database relations and a model to use client side and server side.
 */
@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy="organisation")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @OneToMany(mappedBy="organisation")
    private List<ApplicationFinance> applicationFinances = new ArrayList<>();

    public Organisation() {

    }

    public Organisation(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    @JsonIgnore
    public List<ApplicationFinance> getApplicationFinances() {
        return applicationFinances;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
    }
}
