package org.innovateuk.ifs.project.managestate.form;

import org.innovateuk.ifs.project.resource.ProjectState;

import javax.validation.constraints.NotNull;

import static org.innovateuk.ifs.project.resource.ProjectState.*;

public class ManageProjectStateForm {

    @NotNull(message = "{validation.manage.project.state.required}")
    private ProjectState state;
    private Boolean confirmationOffline;
    private Boolean confirmationWithdrawn;
    private Boolean confirmationCompleteOffline;
    private String onHoldReason;
    private String onHoldDetails;

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

    public String getOnHoldReason() {
        return onHoldReason;
    }

    public void setOnHoldReason(String onHoldReason) {
        this.onHoldReason = onHoldReason;
    }

    public String getOnHoldDetails() {
        return onHoldDetails;
    }

    public void setOnHoldDetails(String onHoldDetails) {
        this.onHoldDetails = onHoldDetails;
    }

    public boolean isCompletedOffline() {
        return COMPLETED_OFFLINE.equals(state);
    }

    public boolean isHandledOffline() {
        return HANDLED_OFFLINE.equals(state);
    }

    public boolean isWithdrawn() {
        return WITHDRAWN.equals(state);
    }

    public boolean isOnHold() {
        return ON_HOLD.equals(state);
    }
}
