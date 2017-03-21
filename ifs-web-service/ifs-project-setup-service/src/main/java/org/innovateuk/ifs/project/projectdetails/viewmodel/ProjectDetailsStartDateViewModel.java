package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;

/**
 * View model that backs the Project Details - Start Date page
 */
public class ProjectDetailsStartDateViewModel implements BasicProjectDetailsViewModel {
    private Long applicationId;
    private Long projectId;
    private String projectName;
    private long projectDurationInMonths;

    public ProjectDetailsStartDateViewModel(ProjectResource project) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.projectDurationInMonths = project.getDurationInMonths();
        this.applicationId = project.getApplication();
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

    public Long getApplicationId() {
        return applicationId;
    }
}
