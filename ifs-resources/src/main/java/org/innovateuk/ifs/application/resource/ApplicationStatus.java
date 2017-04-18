package org.innovateuk.ifs.application.resource;


import org.innovateuk.ifs.workflow.resource.State;

import java.util.Arrays;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Java enumeration of the current available Application workflow statuses.
 * The value of these entries are used when saving to the database.
 */
public enum ApplicationStatus {
    CREATED(ApplicationState.CREATED), // initial state
    SUBMITTED(ApplicationState.SUBMITTED),
    APPROVED(ApplicationState.APPROVED),
    REJECTED(ApplicationState.REJECTED),
    OPEN(ApplicationState.OPEN);

    static Map<ApplicationState, ApplicationStatus> stateMap =
            Arrays.stream(values()).collect(toMap(ApplicationStatus::fromApplicationState, identity()));

    private final ApplicationState applicationState;

    ApplicationStatus(final ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    public ApplicationState fromApplicationState() {
        return applicationState;
    }

    public State toBackingState() {
        return fromApplicationState().getBackingState();
    }

    public static ApplicationStatus fromApplicationState(ApplicationState applicationState) {
        return stateMap.get(applicationState);
    }
}
