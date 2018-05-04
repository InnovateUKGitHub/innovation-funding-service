package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.util.enums.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Viability process.
 */
public enum ViabilityState implements ProcessState, IdentifiableEnum<ViabilityState> {

    REVIEW(21, State.NOT_VERIFIED),
    NOT_APPLICABLE(22, State.NOT_APPLICABLE),
    APPROVED(23, State.ACCEPTED);

    private final long id;
    private final State backingState;

    ViabilityState(long id, State backingState) {
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
        return simpleMap(ViabilityState.values(), ProcessState::getBackingState);
    }

    public static ViabilityState fromState(State state) {
        return ProcessState.fromState(ViabilityState.values(), state);
    }

    @Override
    public long getId() {
        return id;
    }
}