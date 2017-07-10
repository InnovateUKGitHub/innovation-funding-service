package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.workflow.resource.ProcessStates;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public enum AssessmentStates implements ProcessStates {
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

    private static final Map<String, AssessmentStates> assessmentStatesMap;

    static {
        assessmentStatesMap = new HashMap<>();

        for (AssessmentStates assessmentState : AssessmentStates.values()) {
            assessmentStatesMap.put(assessmentState.getStateName(), assessmentState);
        }
    }

    // creates the enum with the chosen type.
    AssessmentStates(State backingState) {
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

    public static AssessmentStates getByState(String state) {
        return assessmentStatesMap.get(state);
    }

    public static AssessmentStates fromState(State state) {
        return ProcessStates.fromState(AssessmentStates.values(), state);
    }

    public static Set<State> getBackingStates(Set<AssessmentStates> states) {
        return simpleMapSet(states, AssessmentStates::getBackingState);
    }

    public static Set<State> getBackingStates(List<AssessmentStates> states) {
        return states.stream()
                .map(AssessmentStates::getBackingState)
                .collect(Collectors.toSet());
    }
}
