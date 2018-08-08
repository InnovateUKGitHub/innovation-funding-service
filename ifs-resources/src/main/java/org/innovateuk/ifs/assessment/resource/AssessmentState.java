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

public enum AssessmentState implements ProcessState, IdentifiableEnum<AssessmentState> {
    CREATED(19, State.CREATED),
    PENDING(1, State.PENDING),
    WITHDRAWN(20, State.WITHDRAWN),
    REJECTED(3, State.REJECTED),
    ACCEPTED(12, State.ACCEPTED),
    OPEN(2, State.OPEN),
    READY_TO_SUBMIT(4, State.READY_TO_SUBMIT),
    SUBMITTED(5, State.SUBMITTED),

    DECIDE_IF_READY_TO_SUBMIT(-1, State.DECIDE_IF_READY_TO_SUBMIT); // pseudo state?

    private static final Map<String, AssessmentState> assessmentStatesMap;

    private final long id;
    private final State backingState;

    static {
        assessmentStatesMap = new HashMap<>();

        for (AssessmentState assessmentState : AssessmentState.values()) {
            assessmentStatesMap.put(assessmentState.getStateName(), assessmentState);
        }
    }

    AssessmentState(long id, State backingState) {
        this.id = id;
        this.backingState = backingState;
    }

    public static final ImmutableSet<AssessmentState> acceptedAssessmentStates = immutableEnumSet(
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
}