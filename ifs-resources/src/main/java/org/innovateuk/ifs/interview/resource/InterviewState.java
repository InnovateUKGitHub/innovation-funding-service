package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public enum InterviewState implements ProcessState {
    CREATED(State.CREATED),
    PENDING(State.PENDING),
    REJECTED(State.REJECTED),
    ACCEPTED(State.ACCEPTED),
    WITHDRAWN(State.WITHDRAWN);

    private final State backingState;

    private static final Map<String, InterviewState> assessmentInterviewStateMap =
            Stream.of(values()).collect(toMap(InterviewState::getStateName, identity()));

    // creates the enum with the chosen type.
    InterviewState(State backingState) {
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
        return assessmentInterviewStateMap.keySet();
    }

    public static InterviewState getByState(String state) {
        return assessmentInterviewStateMap.get(state);
    }

    public static InterviewState fromState(State state) {
        return ProcessState.fromState(values(), state);
    }

    public static Set<State> getBackingStates(Set<InterviewState> states) {
        return simpleMapSet(states, InterviewState::getBackingState);
    }
}