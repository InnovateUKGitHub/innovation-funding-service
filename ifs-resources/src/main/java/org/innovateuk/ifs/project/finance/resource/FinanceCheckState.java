package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.ProcessStates;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum FinanceCheckState implements ProcessStates {

    PENDING(State.PENDING),
    APPROVED(State.ACCEPTED);

    //the status string value
    private State backingState;

    // creates the enum with the chosen type.
    FinanceCheckState(State backingState) {
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
        return simpleMap(FinanceCheckState.values(), ProcessStates::getBackingState);
    }

    public static FinanceCheckState fromState(State state) {
        return ProcessStates.fromState(FinanceCheckState.values(), state);
    }
}
