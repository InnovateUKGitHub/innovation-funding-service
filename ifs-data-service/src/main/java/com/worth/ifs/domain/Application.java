package com.worth.ifs.domain;

/**
 * Created by wouter on 29/07/15.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User object for saving user details to the db. This is used so we can check authentication and authorization.
 */
@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    @OneToMany(mappedBy="application")
    private List<UserApplicationRole> userApplicationRoles = new ArrayList<UserApplicationRole>();

    @ManyToOne
    @JoinColumn(name="processStatusId", referencedColumnName="id")
    private ProcessStatus processStatus;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<UserApplicationRole> getUserApplicationRoles() {
        return userApplicationRoles;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }
}
