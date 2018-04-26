package org.innovateuk.ifs.application.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum ApplicationState implements ProcessState {
    CREATED(State.CREATED), // initial state
    SUBMITTED(State.SUBMITTED),
    INELIGIBLE(State.NOT_APPLICABLE),
    INELIGIBLE_INFORMED(State.NOT_APPLICABLE_INFORMED),
    APPROVED(State.ACCEPTED),
    REJECTED(State.REJECTED),
    OPEN(State.OPEN),
    IN_PANEL(State.IN_PANEL);
    final State backingState;

    public static final ImmutableSet<ApplicationState> submittedAndFinishedStates = Sets.immutableEnumSet(
            SUBMITTED,
            INELIGIBLE,
            APPROVED,
            REJECTED,
            INELIGIBLE_INFORMED);

    public static final ImmutableSet<ApplicationState> inProgressStates = Sets.immutableEnumSet(
            CREATED,
            OPEN);

    public static final ImmutableSet<ApplicationState> finishedStates = Sets.immutableEnumSet(
            APPROVED,
            REJECTED,
            INELIGIBLE_INFORMED
    );

    public static final ImmutableSet<ApplicationState> submittedStates = Sets.immutableEnumSet(
            SUBMITTED,
            INELIGIBLE
    );

    ApplicationState(State backingState) {
        this.backingState = backingState;
    }

    public String getStateName() {
        return backingState.name();
    }

    public State getBackingState() {
        return backingState;
    }

    public static List<State> getBackingStates() {
        return simpleMap(ApplicationState.values(), ProcessState::getBackingState);
    }

    public static ApplicationState fromState(State state) {
        return ProcessState.fromState(ApplicationState.values(), state);
    }
}
