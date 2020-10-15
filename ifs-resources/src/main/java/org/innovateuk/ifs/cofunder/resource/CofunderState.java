package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

public enum CofunderState implements ProcessState, IdentifiableEnum {
    CREATED(56, State.CREATED),
    ACCEPTED(57, State.ACCEPTED),
    REJECTED(58, State.REJECTED);

    private final long id;
    private final State backingState;

    CofunderState(long id, State backingState) {
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

    public boolean isCreated() {
        return this == CREATED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

    public boolean isAccepted() {
        return this == ACCEPTED;
    }
}