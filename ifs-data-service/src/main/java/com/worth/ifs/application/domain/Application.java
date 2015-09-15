package com.worth.ifs.application.domain;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Application defines database relations and a model to use client side and server side.
 */
@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private LocalDate startDate;
    private Long durationInMonths; // in months

    public void setId(Long id) { this.id = id; }

    @OneToMany(mappedBy="application")
    private List<ProcessRole> processRoles = new ArrayList<ProcessRole>();

    @OneToMany(mappedBy="application")
    private List<ApplicationFinance> applicationFinances = new ArrayList<ApplicationFinance>();

    @ManyToOne
    @JoinColumn(name="applicationStatusId", referencedColumnName="id")
    private ApplicationStatus applicationStatus;

    @ManyToOne
    @JoinColumn(name="competition", referencedColumnName="id")
    private Competition competition;

    public Application() {
    }

    public Application(Competition competition, String name, List<ProcessRole> processRoles, ApplicationStatus applicationStatus, Long id) {
        this.competition = competition;
        this.name = name;
        this.processRoles = processRoles;
        this.applicationStatus = applicationStatus;
        this.id = id;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Application;
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

    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Application(Long id, String name, ApplicationStatus applicationStatus) {
        this.id = id;
        this.name = name;
        this.applicationStatus = applicationStatus;
    }

    public void addUserApplicationRole(ProcessRole... processRoles){
        if(this.processRoles == null){
            this.processRoles = new ArrayList<>();
        }
        this.processRoles.addAll(Arrays.asList(processRoles));


    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }
}
