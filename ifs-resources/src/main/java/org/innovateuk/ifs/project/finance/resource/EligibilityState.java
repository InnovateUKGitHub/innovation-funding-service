package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.util.enums.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;


/**
 * Represents the states that can be transitioned during the Eligibility process.
 */
public enum EligibilityState implements ProcessState, IdentifiableEnum {

    REVIEW(24, State.NOT_VERIFIED),
    NOT_APPLICABLE(25, State.NOT_APPLICABLE),
    APPROVED(26, State.ACCEPTED);

    private final long id;
    private final State backingState;

    EligibilityState(long id, State backingState) {
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
        return simpleMap(EligibilityState.values(), ProcessState::getBackingState);
    }

    public static EligibilityState fromState(State state) {
        return ProcessState.fromState(EligibilityState.values(), state);
    }

    @Override
    public long getId() {
        return id;
    }
}