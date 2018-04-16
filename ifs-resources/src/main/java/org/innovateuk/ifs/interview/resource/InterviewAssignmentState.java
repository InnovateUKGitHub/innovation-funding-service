package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public enum InterviewAssignmentState implements ProcessState {
    CREATED(State.CREATED),
    AWAITING_FEEDBACK_RESPONSE(State.PENDING),
    SUBMITTED_FEEDBACK_RESPONSE(State.SUBMITTED);

    public static final InterviewAssignmentState[] ASSIGNED_STATES =
            { AWAITING_FEEDBACK_RESPONSE, SUBMITTED_FEEDBACK_RESPONSE };

    private final State backingState;

    private static final Map<String, InterviewAssignmentState> assessmentInterviewPanelStates =
            Stream.of(values()).collect(toMap(InterviewAssignmentState::getStateName, identity()));

    // creates the enum with the chosen type.
    InterviewAssignmentState(State backingState) {
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
        return assessmentInterviewPanelStates.keySet();
    }

    public static InterviewAssignmentState getByState(String state) {
        return assessmentInterviewPanelStates.get(state);
    }

    public static InterviewAssignmentState fromState(State state) {
        return ProcessState.fromState(values(), state);
    }

    public static Set<State> getBackingStates(Set<InterviewAssignmentState> states) {
        return simpleMapSet(states, InterviewAssignmentState::getBackingState);
    }
}