package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Viability process.
 */
public enum ViabilityState implements ProcessState, IdentifiableEnum {

    REVIEW(21, State.NOT_VERIFIED, Viability.REVIEW),
    NOT_APPLICABLE(22, State.NOT_APPLICABLE, Viability.NOT_APPLICABLE),
    APPROVED(23, State.ACCEPTED, Viability.APPROVED),
    COMPLETED_OFFLINE(54, State.COMPLETED_OFFLINE, Viability.COMPLETED_OFFLINE),

    COMPLETE_OFFLINE_DECISION(-1, null, null );

    private final long id;
    private final State backingState;
    private final Viability viability;

    ViabilityState(long id, State backingState, Viability viability) {
        this.id = id;
        this.backingState = backingState;
        this.viability = viability;
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

    public Viability getViability() {
        return viability;
    }

    @Override
    public long getId() {
        return id;
    }
}