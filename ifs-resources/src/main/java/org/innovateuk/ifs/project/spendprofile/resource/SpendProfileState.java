package org.innovateuk.ifs.project.spendprofile.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Spend Profile process.
 */
public enum SpendProfileState implements ProcessState, IdentifiableEnum<SpendProfileState> {
    PENDING(39, State.PENDING),
    CREATED(40, State.CREATED),
    SUBMITTED(41, State.SUBMITTED),
    APPROVED(42, State.ACCEPTED),
    REJECTED(43, State.REJECTED);

    private final long id;
    private final State backingState;

    SpendProfileState(long id, State backingState) {
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
        return simpleMap(SpendProfileState.values(), ProcessState::getBackingState);
    }

    public static SpendProfileState fromState(State state) {
        return ProcessState.fromState(SpendProfileState.values(), state);
    }

    @Override
    public long getId() {
        return id;
    }
}