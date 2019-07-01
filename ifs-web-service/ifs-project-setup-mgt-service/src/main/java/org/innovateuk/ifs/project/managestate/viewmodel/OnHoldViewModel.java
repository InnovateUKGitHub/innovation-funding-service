package org.innovateuk.ifs.project.managestate.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;

public class OnHoldViewModel {

    private final long competitionId;
    private final long applicationId;
    private final long projectId;
    private final String projectName;

    public OnHoldViewModel(ProjectResource project) {
        this.competitionId = project.getCompetition();
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.projectName = project.getName();
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }
}
