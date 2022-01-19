package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum FundingRulesState implements ProcessState, IdentifiableEnum {

    REVIEW(61, State.NOT_VERIFIED),
    APPROVED(62, State.ACCEPTED);

    private final long id;
    private final State backingState;

    FundingRulesState(long id, State backingState) {
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
        return simpleMap(FundingRulesState.values(), ProcessState::getBackingState);
    }

    public static FundingRulesState fromState(State state) {
        return ProcessState.fromState(FundingRulesState.values(), state);
    }

    @Override
    public long getId() {
        return id;
    }

    public boolean isInReview() {
        return this == REVIEW;
    }

    public boolean isApproved() {
        return this == APPROVED;
    }
}
