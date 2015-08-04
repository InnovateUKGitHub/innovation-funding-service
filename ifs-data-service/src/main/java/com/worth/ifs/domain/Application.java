package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Application defines database relations and a model to use client side and server side.
 */
@Entity
public class Application {
    public Application(Competition competition, String name, List<UserApplicationRole> userApplicationRoles, ProcessStatus processStatus, long id) {
        this.competition = competition;
        this.name = name;
        this.userApplicationRoles = userApplicationRoles;
        this.processStatus = processStatus;
        this.id = id;
    }

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

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Application(long id, String name, ProcessStatus processStatus) {
        this.id = id;
        this.name = name;
        this.processStatus = processStatus;
    }

    public void addUserApplicationRole(UserApplicationRole... userApplicationRoles){
        if(this.userApplicationRoles == null){
            this.userApplicationRoles = new ArrayList<>();
        }
        this.userApplicationRoles.addAll(Arrays.asList(userApplicationRoles));


    }
}
