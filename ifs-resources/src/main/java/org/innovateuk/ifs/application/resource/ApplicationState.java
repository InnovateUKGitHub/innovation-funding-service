package org.innovateuk.ifs.application.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum ApplicationState implements ProcessState {
    CREATED(State.CREATED, "Created"), // initial state
    SUBMITTED(State.SUBMITTED, "Submitted"),
    INELIGIBLE(State.NOT_APPLICABLE, "Ineligible"),
    INELIGIBLE_INFORMED(State.NOT_APPLICABLE_INFORMED, "Ineligible Informed"),
    APPROVED(State.ACCEPTED, "Approved"),
    REJECTED(State.REJECTED, "Rejected"),
    OPEN(State.OPEN, "Open"),
    IN_PANEL(State.IN_PANEL, "In Panel");

    final State backingState;
    private String displayName;

    public static final ImmutableSet<ApplicationState> submittedStates = Sets.immutableEnumSet(ApplicationState.SUBMITTED,
            ApplicationState.INELIGIBLE,
            ApplicationState.APPROVED,
            ApplicationState.REJECTED,
            ApplicationState.INELIGIBLE_INFORMED);

    ApplicationState(State backingState, String displayName) {
        this.backingState = backingState;
        this.displayName = displayName;
    }

/*
    ApplicationState(State backingState) {
        this.backingState = backingState;
    }
*/

    public String getStateName() {
        return backingState.name();
    }

    public State getBackingState() {
        return backingState;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static List<State> getBackingStates() {
        return simpleMap(ApplicationState.values(), ProcessState::getBackingState);
    }

    public static ApplicationState fromState(State state) {
        return ProcessState.fromState(ApplicationState.values(), state);
    }
}
