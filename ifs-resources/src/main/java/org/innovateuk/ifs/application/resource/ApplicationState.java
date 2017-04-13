package org.innovateuk.ifs.application.resource;


import org.innovateuk.ifs.workflow.resource.ProcessStates;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum ApplicationState implements ProcessStates{
    CREATED(State.CREATED), // initial state
    SUBMITTED(State.SUBMITTED),
    APPROVED(State.ACCEPTED),
    REJECTED(State.REJECTED),
    OPEN(State.OPEN);

    final State backingState;

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
        return simpleMap(ApplicationState.values(), ProcessStates::getBackingState);
    }

    public static ApplicationState fromState(State state) {
        return ProcessStates.fromState(ApplicationState.values(), state);
    }
}
