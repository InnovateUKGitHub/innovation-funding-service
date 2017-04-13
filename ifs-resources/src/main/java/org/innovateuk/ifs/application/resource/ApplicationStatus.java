package org.innovateuk.ifs.application.resource;


import org.innovateuk.ifs.workflow.resource.State;

/**
 * Java enumeration of the current available Application workflow statuses.
 * The value of these entries are used when saving to the database.
 */
public enum ApplicationStatus {
    CREATED, // initial state
    SUBMITTED,
    APPROVED,
    REJECTED,
    OPEN;

    @Deprecated
    public ApplicationState toApplicationState() {
        switch (this) {
            case CREATED:
                return ApplicationState.CREATED;
            case OPEN:
                return ApplicationState.OPEN;
            case SUBMITTED:
                return ApplicationState.SUBMITTED;
            case APPROVED:
                return ApplicationState.APPROVED;
            case REJECTED:
                return ApplicationState.REJECTED;
            default:
                throw new IllegalStateException();
        }
    }

    @Deprecated
    public State toBackingState() {
        return toApplicationState().getBackingState();
    }

    @Deprecated
    public static ApplicationStatus toApplicationState(ApplicationState applicationState) {
        switch (applicationState) {
            case CREATED:
                return CREATED;
            case OPEN:
                return OPEN;
            case SUBMITTED:
                return SUBMITTED;
            case APPROVED:
                return APPROVED;
            case REJECTED:
                return REJECTED;
            default:
                throw new IllegalStateException();
        }
    }
}
