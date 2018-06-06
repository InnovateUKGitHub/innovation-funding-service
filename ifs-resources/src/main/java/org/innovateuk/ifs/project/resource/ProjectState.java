package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Project Setup process.
 */
public enum ProjectState implements ProcessState, IdentifiableEnum<ProjectState> {

    SETUP(17, State.PENDING),
    LIVE(18, State.ACCEPTED),
    WITHDRAWN(48, State.WITHDRAWN);

    private final long id;
    private final State backingState;

    ProjectState(long id, State backingState) {
        this.id = id;
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

    @Override
    public long getId() {
        return id;
    }
}