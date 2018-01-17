package org.innovateuk.ifs.assessment.panel.resource;

import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public enum AssessmentReviewState implements ProcessState {
    CREATED(State.CREATED),
    PENDING(State.PENDING),
    REJECTED(State.REJECTED),
    ACCEPTED(State.ACCEPTED),
    CONFLICT_OF_INTEREST(State.CONFLICT_OF_INTEREST),
    WITHDRAWN(State.WITHDRAWN);

    private final State backingState;

    private static final Map<String, AssessmentReviewState> assessmentPanelApplicationInviteStateMap =
            Stream.of(values()).collect(toMap(AssessmentReviewState::getStateName, identity()));

    // creates the enum with the chosen type.
    AssessmentReviewState(State backingState) {
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

    public static Set<String> getStates() {
        return assessmentPanelApplicationInviteStateMap.keySet();
    }

    public static AssessmentReviewState getByState(String state) {
        return assessmentPanelApplicationInviteStateMap.get(state);
    }

    public static AssessmentReviewState fromState(State state) {
        return ProcessState.fromState(values(), state);
    }

    public static Set<State> getBackingStates(Set<AssessmentReviewState> states) {
        return simpleMapSet(states, AssessmentReviewState::getBackingState);
    }
}