package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.util.enums.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

public enum InterviewState implements ProcessState, IdentifiableEnum<InterviewState> {
    ASSIGNED(49, State.ASSIGNED);

    private final long id;
    private final State backingState;

    InterviewState(long id, State backingState) {
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

    @Override
    public long getId() {
        return id;
    }
}