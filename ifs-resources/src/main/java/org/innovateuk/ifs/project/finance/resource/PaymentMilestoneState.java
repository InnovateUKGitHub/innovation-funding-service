package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the {@link org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneProcess} process.
 */
public enum PaymentMilestoneState implements ProcessState, IdentifiableEnum {

    REVIEW(59, State.NOT_VERIFIED),
    APPROVED(60, State.ACCEPTED);

    private final long id;
    private final State backingState;

    PaymentMilestoneState(long id, State backingState) {
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

    public boolean isInReview() {
        return this == REVIEW;
    }

    public boolean isNotApplicable(){
        return this == APPROVED;
    }
}
