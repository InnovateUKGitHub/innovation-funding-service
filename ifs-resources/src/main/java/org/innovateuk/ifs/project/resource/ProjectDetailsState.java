package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.*;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum ProjectDetailsState implements ProcessState {

    PENDING(State.PENDING),
    DECIDE_IF_READY_TO_SUBMIT(State.PENDING),
    SUBMITTED(State.SUBMITTED);

    //the status string value
    private State backingState;

    // creates the enum with the chosen type.
    ProjectDetailsState(State backingState) {
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
        return simpleMap(ProjectDetailsState.values(), ProcessState::getBackingState);
    }

    public static ProjectDetailsState fromState(State state) {
        return ProcessState.fromState(ProjectDetailsState.values(), state);
    }
}
