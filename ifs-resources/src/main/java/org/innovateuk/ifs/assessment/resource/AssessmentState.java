package org.innovateuk.ifs.assessment.resource;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.immutableEnumSet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public enum AssessmentState implements ProcessState, IdentifiableEnum {
    CREATED(19, State.CREATED, 1),
    PENDING(1, State.PENDING, 2),
    WITHDRAWN(20, State.WITHDRAWN, 3),
    REJECTED(3, State.REJECTED, 4),
    ACCEPTED(12, State.ACCEPTED, 5),
    OPEN(2, State.OPEN, 6),
    READY_TO_SUBMIT(4, State.READY_TO_SUBMIT, 7),
    SUBMITTED(5, State.SUBMITTED, 8),

    DECIDE_IF_READY_TO_SUBMIT(-1, State.DECIDE_IF_READY_TO_SUBMIT, 9); // pseudo state?

    private static final Map<String, AssessmentState> assessmentStatesMap;

    private final long id;
    private final State backingState;
    private final int priority;

    static {
        assessmentStatesMap = new HashMap<>();

        for (AssessmentState assessmentState : AssessmentState.values()) {
            assessmentStatesMap.put(assessmentState.getStateName(), assessmentState);
        }
    }

    AssessmentState(long id, State backingState, int priority) {
        this.id = id;
        this.backingState = backingState;
        this.priority = priority;
    }

    public static final ImmutableSet<AssessmentState> acceptedAssessmentStates = immutableEnumSet(
            ACCEPTED,
            OPEN,
            READY_TO_SUBMIT,
            SUBMITTED);

    public static final ImmutableSet<AssessmentState> assignedAssessmentStates = immutableEnumSet(
            CREATED,
            PENDING,
            ACCEPTED,
            OPEN,
            READY_TO_SUBMIT,
            SUBMITTED);

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

    @Override
    public long getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }
}