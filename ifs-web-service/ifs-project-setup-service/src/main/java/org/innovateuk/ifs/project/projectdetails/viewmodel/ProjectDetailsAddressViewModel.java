package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;

/**
 * The view model that backs the project address
 */
public class ProjectDetailsAddressViewModel implements BasicProjectDetailsViewModel {
    private Long applicationId;
    private Long projectId;
    private String projectName;
    private boolean isLoanProject;
    private boolean isCollaborativeProject;

    public ProjectDetailsAddressViewModel(ProjectResource projectResource) {
        this.projectId = projectResource.getId();
        this.projectName = projectResource.getName();
        this.applicationId = projectResource.getApplication();
        this.isLoanProject = projectResource.isLoanProject();
        this.isCollaborativeProject = projectResource.isCollaborativeProject();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public boolean isLoanProject() {
        return isLoanProject;
    }

    public boolean isCollaborativeProject() {
        return isCollaborativeProject;
    }
}
