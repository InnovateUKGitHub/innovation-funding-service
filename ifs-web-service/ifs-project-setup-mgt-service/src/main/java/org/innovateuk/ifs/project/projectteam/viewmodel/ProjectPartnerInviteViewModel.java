package org.innovateuk.ifs.project.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectPartnerInviteViewModel {

    private final long projectId;
    private final long applicationId;
    private final long competitionId;
    private final String projectName;

    public ProjectPartnerInviteViewModel(ProjectResource project) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.competitionId = project.getCompetition();
        this.projectName = project.getName();
    }

    public long getProjectId() {
        return projectId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }
}
