package org.innovateuk.ifs.project.grantofferletter.resource;

import org.innovateuk.ifs.workflow.resource.ProcessStates;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Grant Offer Letter process.
 */
public enum GrantOfferLetterState implements ProcessStates {

    PENDING(State.PENDING),
    SENT(State.ASSIGNED),
    READY_TO_APPROVE(State.READY_TO_SUBMIT),
    APPROVED(State.ACCEPTED);

    //the status string value
    private State backingState;

    // creates the enum with the chosen type.
    GrantOfferLetterState(State backingState) {
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
        return simpleMap(GrantOfferLetterState.values(), ProcessStates::getBackingState);
    }

    public static GrantOfferLetterState fromState(State state) {
        return ProcessStates.fromState(GrantOfferLetterState.values(), state);
    }
}
