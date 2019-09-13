package org.innovateuk.ifs.project.setupcomplete.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;

public class ProjectSetupCompleteViewModel {

    private final long projectId;
    private final long applicationId;
    private final long competitionId;
    private final String projectName;

    private final ProjectState state;

    public ProjectSetupCompleteViewModel(ProjectResource project) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.competitionId = project.getCompetition();
        this.projectName = project.getName();
        this.state = project.getProjectState();
    }

    public long getProjectId() {
        return projectId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getProjectName() {
        return projectName;
    }

    public ProjectState getState() {
        return state;
    }

    public boolean isReadonly() {
        return !state.isActive();
    }
}
