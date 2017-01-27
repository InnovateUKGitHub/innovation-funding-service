package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.ProcessStates;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Viability process.
 */
public enum ViabilityState implements ProcessStates {

    REVIEW(State.NOT_VERIFIED),
    NOT_APPLICABLE(State.NOT_APPLICABLE),
    APPROVED(State.ACCEPTED);

    //the status string value
    private State backingState;

    // creates the enum with the chosen type.
    ViabilityState(State backingState) {
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
        return simpleMap(ViabilityState.values(), ProcessStates::getBackingState);
    }

    public static ViabilityState fromState(State state) {
        return ProcessStates.fromState(ViabilityState.values(), state);
    }
}

