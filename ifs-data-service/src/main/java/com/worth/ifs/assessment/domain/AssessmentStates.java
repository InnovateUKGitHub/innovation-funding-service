package com.worth.ifs.assessment.domain;

import com.worth.ifs.workflow.domain.ProcessStates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum AssessmentStates implements ProcessStates {
    // All types of status
    PENDING("pending", 1),
    REJECTED("rejected", 2),
    OPEN("open", 3),
    ASSESSED("assessed", 4),
    SUBMITTED("submitted", 5);

    //the status string value
    private final String state;
    private final int ordinal;

    private static final Map<String, AssessmentStates> assessmentStatesMap;

    static {
        assessmentStatesMap = new HashMap<>();

        for (AssessmentStates assessmentState : AssessmentStates.values()) {
            assessmentStatesMap.put(assessmentState.getState(), assessmentState);
        }
    }

    //creates the enum with the choosen type.
    AssessmentStates(String value, int ordinal) {
        this.state = value;
        this.ordinal = ordinal;
    }

    public String getState() {
        return state;
    }

    public int getOrdinal() { return ordinal; }

    public static Set<String> getStates() {
        Set<String> states = new HashSet<>();
        for(AssessmentStates assessmentState : values()) {
            states.add(assessmentState.getState());
        }
        return states;
    }

    public static AssessmentStates getByState(String state) {
       return  assessmentStatesMap.get(state);
    }
}
