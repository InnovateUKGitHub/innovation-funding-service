package org.innovateuk.ifs.project.spendprofile.resource;

import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Spend Profile process.
 */

public enum SpendProfileState implements ProcessState {
    PENDING(State.PENDING),
    GENERATED(State.CREATED),
    SUBMITTED(State.SUBMITTED),
    APPROVED(State.ACCEPTED),
    REJECTED(State.REJECTED);

    //the status string value
    private State backingState;

    // creates the enum with the chosen type.
    SpendProfileState(State backingState) {
        this.backingState = backingState;
    }

    @Override
    public String getStateName() {
        return backingState.name();
    }

    @Override
    public State getBackingState() {
        return backingState;
    }

    public static List<State> getBackingStates() {
        return simpleMap(SpendProfileState.values(), ProcessState::getBackingState);
    }

    public static SpendProfileState fromState(State state) {
        return ProcessState.fromState(SpendProfileState.values(), state);
    }
}
