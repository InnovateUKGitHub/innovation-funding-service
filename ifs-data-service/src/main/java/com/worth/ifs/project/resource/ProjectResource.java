package com.worth.ifs.project.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Digits;
import java.time.LocalDate;
import java.util.List;

import static com.worth.ifs.application.resource.ApplicationResource.formatter;

public class ProjectResource {
    private static final int MAX_DURATION_IN_MONTHS_DIGITS = 2;

    private Long id;
    private LocalDate targetStartDate;
    private Long address;
    private String name;
    private List<Long> projectUsers;

    @Digits(integer = MAX_DURATION_IN_MONTHS_DIGITS, fraction = 0, message="{validation.application.details.duration.in.months.max.digits}")
    private Long durationInMonths;

    private Long projectManager;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTargetStartDate() {
        return targetStartDate;
    }

    public void setTargetStartDate(LocalDate targetStartDate) {
        this.targetStartDate = targetStartDate;
    }

    public Long getAddress() {
        return address;
    }

    public void setAddress(Long address) {
        this.address = address;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public Long getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(Long projectManager) {
        this.projectManager = projectManager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(List<Long> projectUsers) {
        this.projectUsers = projectUsers;
    }

    @JsonIgnore
    public String getFormattedId(){
        return formatter.format(id);
    }
}
