package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public enum AssessmentState implements ProcessState {
    // All types of status
    CREATED(State.CREATED),
    PENDING(State.PENDING),
    WITHDRAWN(State.WITHDRAWN),
    REJECTED(State.REJECTED),
    ACCEPTED(State.ACCEPTED),
    OPEN(State.OPEN),
    DECIDE_IF_READY_TO_SUBMIT(State.DECIDE_IF_READY_TO_SUBMIT),
    READY_TO_SUBMIT(State.READY_TO_SUBMIT),
    SUBMITTED(State.SUBMITTED);

    private State backingState;

    private static final Map<String, AssessmentState> assessmentStatesMap;

    static {
        assessmentStatesMap = new HashMap<>();

        for (AssessmentState assessmentState : AssessmentState.values()) {
            assessmentStatesMap.put(assessmentState.getStateName(), assessmentState);
        }
    }

    // creates the enum with the chosen type.
    AssessmentState(State backingState) {
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
        return assessmentStatesMap.keySet();
    }

    public static AssessmentState getByState(String state) {
        return assessmentStatesMap.get(state);
    }

    public static AssessmentState fromState(State state) {
        return ProcessState.fromState(AssessmentState.values(), state);
    }

    public static Set<State> getBackingStates(Set<AssessmentState> states) {
        return simpleMapSet(states, AssessmentState::getBackingState);
    }

    public static Set<State> getBackingStates(List<AssessmentState> states) {
        return simpleMapSet(states, AssessmentState::getBackingState);
    }
}
