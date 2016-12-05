package com.worth.ifs.project.resource;

import com.worth.ifs.workflow.resource.ProcessStates;
import com.worth.ifs.workflow.resource.State;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Project Setup process.
 */
public enum ProjectState implements ProcessStates {

    SETUP(State.PENDING),
    LIVE(State.ACCEPTED);

    //the status string value
    private State backingState;

    // creates the enum with the chosen type.
    ProjectState(State backingState) {
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
        return simpleMap(ProjectState.values(), ProcessStates::getBackingState);
    }

    public static ProjectState fromState(State state) {
        return ProcessStates.fromState(ProjectState.values(), state);
    }
}

