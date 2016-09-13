package com.worth.ifs.project.resource;

import com.worth.ifs.workflow.resource.ProcessStates;
import com.worth.ifs.workflow.resource.State;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum ProjectDetailsState implements ProcessStates {

    PENDING(State.PENDING),
    READY_TO_SUBMIT(State.READY_TO_SUBMIT),
    SUBMITTED(State.SUBMITTED);

    //the status string value
    private State backingState;

    private static final Map<String, ProjectDetailsState> assessmentStatesMap;

    static {
        assessmentStatesMap = new HashMap<>();

        for (ProjectDetailsState assessmentState : ProjectDetailsState.values()) {
            assessmentStatesMap.put(assessmentState.getStateName(), assessmentState);
        }
    }

    // creates the enum with the chosen type.
    ProjectDetailsState(State backingState) {
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

    public static ProjectDetailsState getByState(String state) {
       return  assessmentStatesMap.get(state);
    }

    public static ProjectDetailsState fromState(State state) {
        return ProcessStates.fromState(ProjectDetailsState.values(), state);
    }
}
