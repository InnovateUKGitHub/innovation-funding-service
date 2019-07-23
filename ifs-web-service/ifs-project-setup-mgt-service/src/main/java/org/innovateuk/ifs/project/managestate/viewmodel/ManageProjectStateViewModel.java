package org.innovateuk.ifs.project.managestate.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;

import static org.innovateuk.ifs.project.resource.ProjectState.*;

public class ManageProjectStateViewModel {

    private final long competitionId;
    private final long applicationId;
    private final long projectId;
    private final String projectName;
    private final ProjectState state;
    private final boolean ifsAdmin;

    public ManageProjectStateViewModel(ProjectResource project, boolean ifsAdmin) {
        this.competitionId = project.getCompetition();
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.state = project.getProjectState();
        this.ifsAdmin = ifsAdmin;
    }

    public long getApplicationId() {
        return applicationId;
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

    public boolean isIfsAdmin() {
        return ifsAdmin;
    }

    /* view logic */
    public boolean isCompletedOffline() {
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

    public boolean isOnHold() {
        return ON_HOLD.equals(state);
    }

    public boolean isLive() {
        return LIVE.equals(state);
    }

    public boolean canCompleteOffline() {
        return isHandledOffline();
    }

    public boolean canHandleOffline() {
        return isInSetup() || isOnHold();
    }

    public boolean canPutOnHold() {
        return isInSetup();
    }

    public boolean canWithdraw() {
        return isInSetup() || isHandledOffline() || isOnHold();
    }

    public boolean isEndState() { return isCompletedOffline() || isLive() || isWithdrawn(); }

    public boolean cantChangeState() {
        return isEndState() || (!ifsAdmin && isOnHold());
    }
}
