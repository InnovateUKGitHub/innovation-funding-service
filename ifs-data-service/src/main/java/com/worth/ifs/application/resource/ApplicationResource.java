package com.worth.ifs.application.resource;

import org.springframework.hateoas.core.Relation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Relation(value="application", collectionRelation="applications")
public class ApplicationResource {

    private Long id;
    private String name;
    private LocalDate startDate;
    private Long durationInMonths; // in months
    private List<Long> processRoles = new ArrayList<>();
    private List<Long> applicationFinances = new ArrayList<>();
    private Long applicationStatus;
    private Long competition;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Long getDurationInMonths() {
        return this.durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public List<Long> getProcessRoles() {
        return this.processRoles;
    }

    public void setProcessRoles(List<Long> processRoles) {
        this.processRoles = processRoles;
    }

    public List<Long> getApplicationFinances() {
        return this.applicationFinances;
    }

    public void setApplicationFinances(List<Long> applicationFinances) {
        this.applicationFinances = applicationFinances;
    }

    public Long getApplicationStatus() {
        return this.applicationStatus;
    }

    public void setApplicationStatus(Long applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Long getCompetition() {
        return this.competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }
}
