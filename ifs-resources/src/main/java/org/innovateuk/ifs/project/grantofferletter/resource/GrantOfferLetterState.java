package org.innovateuk.ifs.project.grantofferletter.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Grant Offer Letter process.
 */
public enum GrantOfferLetterState implements ProcessState, IdentifiableEnum {

    PENDING(13, State.PENDING),
    SENT(14, State.ASSIGNED),
    READY_TO_APPROVE(15, State.READY_TO_SUBMIT),
    APPROVED(16, State.ACCEPTED);

    private final long id;
    private final State backingState;

    GrantOfferLetterState(long id, State backingState) {
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
        return simpleMap(GrantOfferLetterState.values(), ProcessState::getBackingState);
    }

    public static GrantOfferLetterState fromState(State state) {
        return ProcessState.fromState(GrantOfferLetterState.values(), state);
    }

    @Override
    public long getId() {
        return id;
    }
}