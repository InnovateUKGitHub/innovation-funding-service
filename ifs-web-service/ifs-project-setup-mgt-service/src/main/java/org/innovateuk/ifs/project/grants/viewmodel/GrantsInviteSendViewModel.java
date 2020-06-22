package org.innovateuk.ifs.project.grants.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;

public class GrantsInviteSendViewModel {
    private long applicationId;
    private long projectId;
    private String projectName;

    public GrantsInviteSendViewModel(ProjectResource project) {
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.projectName = project.getName();
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getProjectId() {
        return projectId;
    }
}
