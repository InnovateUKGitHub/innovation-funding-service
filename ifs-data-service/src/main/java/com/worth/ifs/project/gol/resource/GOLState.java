package com.worth.ifs.project.gol.resource;

import com.worth.ifs.workflow.resource.ProcessStates;
import com.worth.ifs.workflow.resource.State;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

public enum GOLState implements ProcessStates {

    PENDING(State.PENDING),
    SENT(State.ASSIGNED),
    READY_TO_APPROVE(State.READY_TO_SUBMIT),
    APPROVED(State.ACCEPTED);

    //the status string value
    private State backingState;

    // creates the enum with the chosen type.
    GOLState(State backingState) {
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
        return simpleMap(GOLState.values(), ProcessStates::getBackingState);
    }

    public static GOLState fromState(State state) {
        return ProcessStates.fromState(GOLState.values(), state);
    }
}
