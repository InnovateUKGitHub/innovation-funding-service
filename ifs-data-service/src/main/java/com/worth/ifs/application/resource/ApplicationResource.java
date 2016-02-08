package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Relation(value="application", collectionRelation="applications")
public class ApplicationResource {

    private Long id;
    private String name;
    private LocalDate startDate;
    private Long durationInMonths;
    private List<Long> processRoles = new ArrayList<>();
    private List<Long> applicationFinances = new ArrayList<>();
    private Long applicationStatus;
    private Long competition;
    private List<Long> invites;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Long> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<Long> processRoles) {
        this.processRoles = processRoles;
    }

    public List<Long> getApplicationFinances() {
        return applicationFinances;
    }

    public void setApplicationFinances(List<Long> applicationFinances) {
        this.applicationFinances = applicationFinances;
    }

    public Long getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(Long applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    @JsonIgnore
    public List<Long> getInvites() {
        return invites;
    }

    public void setInvites(List<Long> invites) {
        this.invites = invites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationResource that = (ApplicationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(startDate, that.startDate)
                .append(durationInMonths, that.durationInMonths)
                .append(processRoles, that.processRoles)
                .append(applicationFinances, that.applicationFinances)
                .append(applicationStatus, that.applicationStatus)
                .append(competition, that.competition)
                .append(invites, that.invites)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(startDate)
                .append(durationInMonths)
                .append(processRoles)
                .append(applicationFinances)
                .append(applicationStatus)
                .append(competition)
                .append(invites)
                .toHashCode();
    }
}
