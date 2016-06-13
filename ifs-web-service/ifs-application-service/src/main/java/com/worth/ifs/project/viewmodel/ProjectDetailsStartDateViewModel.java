package com.worth.ifs.project.viewmodel;

import com.worth.ifs.project.resource.ProjectResource;

/**
 * View model that backs the Project Details - Start Date page
 */
public class ProjectDetailsStartDateViewModel {
    private Long projectId;
    private String projectNumber;
    private String projectName;
    private long projectDurationInMonths;

    public ProjectDetailsStartDateViewModel(ProjectResource project) {
        this.projectId = project.getId();
        this.projectNumber = project.getFormattedId();
        this.projectName = project.getName();
        this.projectDurationInMonths = project.getDurationInMonths();
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

    public Long getProjectId() {
        return projectId;
    }
}
