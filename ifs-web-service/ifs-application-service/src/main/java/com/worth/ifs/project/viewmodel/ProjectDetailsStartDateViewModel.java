package com.worth.ifs.project.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;

import java.time.LocalDate;

/**
 * View model that backs the Project Details - Start Date page
 */
public class ProjectDetailsStartDateViewModel {

    private String projectNumber;
    private String projectName;
    private long projectDurationInMonths;
    private LocalDate projectStartDate;

    public ProjectDetailsStartDateViewModel(ApplicationResource project) {
        this.projectNumber = project.getFormattedId();
        this.projectName = project.getApplicationDisplayName();
        this.projectDurationInMonths = project.getDurationInMonths();
        this.projectStartDate = LocalDate.of(project.getStartDate().getYear(), project.getStartDate().getMonth(), 1);
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getProjectDurationInMonths() {
        return projectDurationInMonths;
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }
}
