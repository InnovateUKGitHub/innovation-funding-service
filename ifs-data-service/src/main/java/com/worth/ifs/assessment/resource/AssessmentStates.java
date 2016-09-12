package com.worth.ifs.assessment.resource;

import com.worth.ifs.workflow.domain.State;
import com.worth.ifs.workflow.resource.ProcessStates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum AssessmentStates implements ProcessStates {
    // All types of status
    PENDING(State.PENDING),
    REJECTED(State.REJECTED),
    OPEN(State.OPEN),
    ASSESSED(State.READY_TO_SUBMIT),
    SUBMITTED(State.SUBMITTED);

    //the status string value
    private State state;

    private static final Map<String, AssessmentStates> assessmentStatesMap;

    static {
        assessmentStatesMap = new HashMap<>();

        for (AssessmentStates assessmentState : AssessmentStates.values()) {
            assessmentStatesMap.put(assessmentState.getState(), assessmentState);
        }
    }

    // creates the enum with the chosen type.
    AssessmentStates(State state) {
        this.state = state;
    }

    @Override
    public String getState() {
        return state.name();
    }

    public static Set<String> getStates() {
        return assessmentStatesMap.keySet();
    }

    public static AssessmentStates getByState(String state) {
       return  assessmentStatesMap.get(state);
    }

    public static AssessmentStates fromState(State state) {
        for (AssessmentStates assessmentState : values()) {
            if (assessmentState.state.equals(state)) {
                return assessmentState;
            }
        }
        return null;
    }
}
