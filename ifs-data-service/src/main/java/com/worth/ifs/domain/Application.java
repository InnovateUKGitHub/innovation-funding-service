package com.worth.ifs.domain;

/**
 * Created by wouter on 29/07/15.
 */

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

    public void setId(long id) {
        this.id = id;
    }

    @OneToMany(mappedBy="application")
    private List<UserApplicationRole> userApplicationRoles = new ArrayList<UserApplicationRole>();

    @ManyToOne
    @JoinColumn(name="processStatusId", referencedColumnName="id")
    private ProcessStatus processStatus;

    @ManyToOne
    @JoinColumn(name="competition", referencedColumnName="id")
    private Competition competition;

    public Application() {
    }

    protected boolean canEqual(Object other) {
        return other instanceof Application;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserApplicationRole> getUserApplicationRoles() {
        return userApplicationRoles;
    }

    public void setUserApplicationRoles(List<UserApplicationRole> userApplicationRoles) {
        this.userApplicationRoles = userApplicationRoles;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }
}
