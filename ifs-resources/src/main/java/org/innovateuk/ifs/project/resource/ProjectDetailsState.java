package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.util.enums.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.*;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum ProjectDetailsState implements ProcessState, IdentifiableEnum<ProjectDetailsState> {

    PENDING(6, State.PENDING),
    DECIDE_IF_READY_TO_SUBMIT(-1, State.PENDING), // pseudo state
    SUBMITTED(8, State.SUBMITTED);

    private final long id;
    private final State backingState;

    ProjectDetailsState(long id, State backingState) {
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
        return simpleMap(ProjectDetailsState.values(), ProcessState::getBackingState);
    }

    public static ProjectDetailsState fromState(State state) {
        return ProcessState.fromState(ProjectDetailsState.values(), state);
    }

    @Override
    public long getId() {
        return id;
    }
}