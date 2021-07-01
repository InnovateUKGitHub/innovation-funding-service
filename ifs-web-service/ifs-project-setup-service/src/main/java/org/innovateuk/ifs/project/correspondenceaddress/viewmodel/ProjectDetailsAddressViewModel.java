package org.innovateuk.ifs.project.correspondenceaddress.viewmodel;

import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;

/**
 * The view model that backs the project address
 */
public class ProjectDetailsAddressViewModel implements BasicProjectDetailsViewModel {
    private Long applicationId;
    private Long projectId;
    private String projectName;
    private boolean collaborativeProject;
    private boolean ktpCompetition;

    public ProjectDetailsAddressViewModel(ProjectResource projectResource, boolean ktpCompetition) {
        this.projectId = projectResource.getId();
        this.projectName = projectResource.getName();
        this.applicationId = projectResource.getApplication();
        this.collaborativeProject = projectResource.isCollaborativeProject();
        this.ktpCompetition = ktpCompetition;
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

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }
}
