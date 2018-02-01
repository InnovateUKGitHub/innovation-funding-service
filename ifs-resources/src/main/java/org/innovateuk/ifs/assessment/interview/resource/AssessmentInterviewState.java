package org.innovateuk.ifs.assessment.interview.resource;

import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public enum AssessmentInterviewState implements ProcessState {
    CREATED(State.CREATED),
    ACCEPTED(State.ACCEPTED),
    WITHDRAWN(State.WITHDRAWN);

    private final State backingState;

    private static final Map<String, AssessmentInterviewState> assessmentInterviewStateMap =
            Stream.of(values()).collect(toMap(AssessmentInterviewState::getStateName, identity()));

    // creates the enum with the chosen type.
    AssessmentInterviewState(State backingState) {
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

    public static AssessmentInterviewState getByState(String state) {
        return assessmentInterviewStateMap.get(state);
    }

    public static AssessmentInterviewState fromState(State state) {
        return ProcessState.fromState(values(), state);
    }

    public static Set<State> getBackingStates(Set<AssessmentInterviewState> states) {
        return simpleMapSet(states, AssessmentInterviewState::getBackingState);
    }
}