package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplicationResource {
    public static final String ID_PATTERN = "#00000000";
    public static final DecimalFormat formatter = new DecimalFormat(ID_PATTERN);

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDateTime submittedDate;
    private Long durationInMonths;
    private List<Long> processRoles = new ArrayList<>();
    private List<Long> applicationFinances = new ArrayList<>();
    private Long applicationStatus;
    private String applicationStatusName;
    private Long competition;
    private String competitionName;
    private List<Long> invites;

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public String getFormattedId(){
        return formatter.format(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getApplicationDisplayName() {
        if(StringUtils.isNotEmpty(name)){
            return name;
        }else{
            return competitionName;
        }
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

    @JsonIgnore
    public void setApplicationStatusConstant(ApplicationStatusConstants applicationStatus) {
        this.applicationStatus = applicationStatus.getId();
        this.applicationStatusName = applicationStatus.getName();
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

    @JsonIgnore
    public boolean isOpen(){
        return ApplicationStatusConstants.OPEN.getId().equals(applicationStatus) || ApplicationStatusConstants.CREATED.getId().equals(applicationStatus);
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

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public String getApplicationStatusName() {
        return applicationStatusName;
    }

    public void setApplicationStatusName(String applicationStatusName) {
        this.applicationStatusName = applicationStatusName;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }
}
