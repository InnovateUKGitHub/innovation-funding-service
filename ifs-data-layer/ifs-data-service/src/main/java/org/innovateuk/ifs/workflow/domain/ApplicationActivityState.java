package org.innovateuk.ifs.workflow.domain;

import org.innovateuk.ifs.util.enums.Identifiable;
import org.innovateuk.ifs.workflow.resource.State;

public enum ApplicationActivityState implements Identifiable, ActivityStateEnum {
    CREATED(27, State.CREATED);

    final long id;
    final State backingState;

    ApplicationActivityState(final long id, final State backingState) {
        this.id = id;
        this.backingState = backingState;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public State getBackingState() {
        return backingState;
    }
}
