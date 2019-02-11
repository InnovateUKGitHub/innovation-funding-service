package org.innovateuk.ifs.project.monitoring;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

public enum MonitoringState implements ProcessState, IdentifiableEnum {

    // TODO add new rows to activity_state and update the ids
    CREATED(1, State.CREATED),
    PENDING(2, State.PENDING),
    WITHDRAWN(3, State.WITHDRAWN),
    REJECTED(4, State.REJECTED),
    ACCEPTED(5, State.ACCEPTED);

    private final long id;
    private final State backingState;

    MonitoringState(long id, State backingState) {
        this.id = id;
        this.backingState = backingState;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getStateName() {
        return backingState.name();
    }

    @Override
    public State getBackingState() {
        return backingState;
    }
}