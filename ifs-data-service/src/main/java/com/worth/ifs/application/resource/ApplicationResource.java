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
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;

@Relation(value="application", collectionRelation="applications")
public class ApplicationResource {
    private Long id;
    private String name;
    private LocalDate startDate;
    private Long durationInMonths; // in months
    private List<Long> processRoleIds = new ArrayList<>();
    private List<ApplicationFinance> applicationFinances = new ArrayList<>();
    private ApplicationStatus applicationStatus;
    private Long competitionId;

    public ApplicationResource() {
        /*default constructor*/}

    public ApplicationResource(Long id, String name, ApplicationStatus applicationStatus) {
        this.id = id;
        this.name = name;
        this.applicationStatus = applicationStatus;
    }

    public ApplicationResource(Competition competition, String name, List<ProcessRole> processRoles, ApplicationStatus applicationStatus, Long id) {
        this.competitionId = competition.getId();
        this.name = name;
        this.processRoleIds = simpleMap(processRoles,ProcessRole::getId);
        this.applicationStatus = applicationStatus;
        this.id = id;
    }

    public ApplicationResource(Application application){
        //TODO make this work without conditionals
        if(application.getCompetition()==null){
            this.competitionId = null;
        }else {
            this.competitionId = application.getCompetition().getId();
        }
        this.name = application.getName();
        if (application.getProcessRoles() == null) {
            this.processRoleIds = new ArrayList<>();
        }else {
            this.processRoleIds = simpleMap(application.getProcessRoles(),ProcessRole::getId);
        }
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
            else if( this.name==null || that.name==null || ! this.name.equals(that.name)){
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

    public List<Long> getProcessRoleIds() {
        return processRoleIds;
    }

    public void setProcessRoleIds(List<Long> processRoleIds) {
        this.processRoleIds = processRoleIds;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long id) {
        this.competitionId = id;
    }

    public void addUserApplicationRole(ProcessRole... processRoles){
        if(this.processRoleIds == null){
            this.processRoleIds = new ArrayList<>();
        }
        this.processRoleIds.addAll(simpleMap(asList(processRoles),ProcessRole::getId));
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
