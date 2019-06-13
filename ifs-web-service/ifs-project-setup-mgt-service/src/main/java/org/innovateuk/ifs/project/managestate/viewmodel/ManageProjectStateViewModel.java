package org.innovateuk.ifs.project.managestate.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;

import static org.innovateuk.ifs.project.resource.ProjectState.*;

public class ManageProjectStateViewModel {

    private final long competitionId;
    private final long projectId;
    private final String projectName;
    private final ProjectState state;

    public ManageProjectStateViewModel(ProjectResource project) {
        this.competitionId = project.getCompetition();
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.state = project.getProjectState();
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }


    /* view logic */
    public boolean isCompleteOffline() {
        return COMPLETED_OFFLINE.equals(state);
    }

    public boolean isHandledOffline() {
        return HANDLED_OFFLINE.equals(state);
    }

    public boolean isWithdrawn() {
        return WITHDRAWN.equals(state);
    }

    public boolean isInSetup() {
        return SETUP.equals(state);
    }

    public boolean canCompleteOffline() {
        return isHandledOffline();
    }

    public boolean canHandleOffline() {
        return isInSetup();
    }

    public boolean canWithdraw() {
        return isInSetup() || isHandledOffline();
    }
}
