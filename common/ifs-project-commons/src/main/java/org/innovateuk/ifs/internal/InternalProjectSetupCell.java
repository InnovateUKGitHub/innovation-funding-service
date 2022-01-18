package org.innovateuk.ifs.internal;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.resource.ProjectState;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.NOT_REQUIRED;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.NOT_STARTED;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;

public class InternalProjectSetupCell {
    private final ProjectActivityStates state;
    private final String url;
    private final Boolean accessible;
    private final ProjectSetupStage stage;
    private final ProjectState projectState;

    public InternalProjectSetupCell(ProjectActivityStates state, String url, Boolean accessible, ProjectSetupStage stage, ProjectState projectState) {
        this.state = state;
        this.url = url;
        this.accessible = accessible;
        this.stage = stage;
        this.projectState = projectState;
    }

    public ProjectActivityStates getState() {
        return state;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getAccessible() {
        return accessible;
    }

    public ProjectSetupStage getStage() {
        return stage;
    }

    /* View logic */
    public String getText() {
        switch (state) {
            case NOT_REQUIRED:
                return "Not required";
            case ACTION_REQUIRED:
                return stage == MONITORING_OFFICER ? "Assign" : "Review";
            case PENDING:
                return stage == PROJECT_DETAILS
                        || stage == PROJECT_TEAM
                        || !projectState.isActive() ? "Incomplete" : "Pending";
            case COMPLETE:
                return stage == MONITORING_OFFICER ? "Assigned" : "Complete";
            case REJECTED:
                return "Rejected";
            case VIEW:
                return "View";
            case NOT_STARTED:
                return "Stage is not yet available";
            default:
                return "";
        }
    }

    public boolean isVisible() {
        return state != NOT_REQUIRED && state != NOT_STARTED;
    }

    public String getStateStyle() {
        switch (state) {
            case NOT_REQUIRED:
                return "na";
            case ACTION_REQUIRED:
                return "action";
            case PENDING:
                return "waiting";
            case COMPLETE:
                return "ok";
            case REJECTED:
                return "rejected";
            case VIEW:
            case NOT_STARTED:
            default:
                return "";
        }
    }
}
