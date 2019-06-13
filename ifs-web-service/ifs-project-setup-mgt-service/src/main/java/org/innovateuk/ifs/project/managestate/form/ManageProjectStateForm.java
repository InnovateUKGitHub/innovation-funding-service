package org.innovateuk.ifs.project.managestate.form;

import org.innovateuk.ifs.project.resource.ProjectState;

import javax.validation.constraints.NotNull;

import static org.innovateuk.ifs.project.resource.ProjectState.*;

public class ManageProjectStateForm {

    @NotNull
    private ProjectState state;
    private Boolean confirmationOffline;
    private Boolean confirmationWithdrawn;
    private Boolean confirmationCompleteOffline;

    public ProjectState getState() {
        return state;
    }

    public void setState(ProjectState state) {
        this.state = state;
    }

    public Boolean getConfirmationOffline() {
        return confirmationOffline;
    }

    public void setConfirmationOffline(Boolean confirmationOffline) {
        this.confirmationOffline = confirmationOffline;
    }

    public Boolean getConfirmationWithdrawn() {
        return confirmationWithdrawn;
    }

    public void setConfirmationWithdrawn(Boolean confirmationWithdrawn) {
        this.confirmationWithdrawn = confirmationWithdrawn;
    }

    public Boolean getConfirmationCompleteOffline() {
        return confirmationCompleteOffline;
    }

    public void setConfirmationCompleteOffline(Boolean confirmationCompleteOffline) {
        this.confirmationCompleteOffline = confirmationCompleteOffline;
    }


    public boolean isCompleteOffline() {
        return COMPLETED_OFFLINE.equals(state);
    }

    public boolean isHandledOffline() {
        return HANDLED_OFFLINE.equals(state);
    }

    public boolean isWithdrawn() {
        return WITHDRAWN.equals(state);
    }
}
