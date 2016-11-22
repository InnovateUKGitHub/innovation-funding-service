package com.worth.ifs.assessment.resource;

import com.worth.ifs.workflow.resource.ProcessStates;
import com.worth.ifs.workflow.resource.State;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum AssessmentStates implements ProcessStates {
    // All types of status
    PENDING(State.PENDING),
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
}
