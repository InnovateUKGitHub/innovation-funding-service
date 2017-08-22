package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Project Setup process.
 */
public enum ProjectState implements ProcessState {

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
        return simpleMap(ProjectState.values(), ProcessState::getBackingState);
    }

    public static ProjectState fromState(State state) {
        return ProcessState.fromState(ProjectState.values(), state);
    }
}

