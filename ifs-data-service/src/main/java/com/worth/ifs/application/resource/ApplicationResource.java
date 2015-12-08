package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Relation(value="application", collectionRelation="applications")
public class ApplicationResource {
    private Long id;
    private String name;
    private LocalDate startDate;
    private Long durationInMonths; // in months
    private List<ProcessRole> processRoles = new ArrayList<>();
    private List<ApplicationFinance> applicationFinances = new ArrayList<>();
    private ApplicationStatus applicationStatus;
    private Competition competition;

    public ApplicationResource() {
        /*default constructor*/}

    public ApplicationResource(Long id, String name, ApplicationStatus applicationStatus) {
        this.id = id;
        this.name = name;
        this.applicationStatus = applicationStatus;
    }

    public ApplicationResource(Competition competition, String name, List<ProcessRole> processRoles, ApplicationStatus applicationStatus, Long id) {
        this.competition = competition;
        this.name = name;
        this.processRoles = processRoles;
        this.applicationStatus = applicationStatus;
        this.id = id;
    }

    public ApplicationResource(Application application){
        this.competition = application.getCompetition();
        this.name = application.getName();
        this.processRoles = application.getProcessRoles();
        this.applicationStatus = application.getApplicationStatus();
        this.id = application.getId();
        this.applicationFinances = application.getApplicationFinances();
        this.startDate = application.getStartDate();
        this.durationInMonths = application.getDurationInMonths();
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof ApplicationResource)){
            return false;
        }else{
            ApplicationResource that = (ApplicationResource) other;
            if(this.name==null && that.name==null){
                return true;
            }
            else if( ! this.name.equals(that.name)){
                return false;
            }
            if(this.id==null && that.id==null){
                return true;
            }
            else if( this.id == null || that.id == null || ! this.id.equals(that.id)){
                return false;
            }
            return true;
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ApplicationResource;
    }

    public void setId(Long id) {
        this.id = id;
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

    @JsonIgnore
    public List<ApplicationFinance> getApplicationFinances() {
        return applicationFinances;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }
}
