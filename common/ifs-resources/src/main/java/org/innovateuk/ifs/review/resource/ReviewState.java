package org.innovateuk.ifs.review.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public enum ReviewState implements ProcessState, IdentifiableEnum {
    CREATED(34, State.CREATED),
    PENDING(35, State.PENDING),
    REJECTED(36, State.REJECTED),
    ACCEPTED(37, State.ACCEPTED),
    CONFLICT_OF_INTEREST(38, State.CONFLICT_OF_INTEREST),
    WITHDRAWN(44, State.WITHDRAWN);

    private final long id;
    private final State backingState;

    private static final Map<String, ReviewState> assessmentReviewStateMap =
            Stream.of(values()).collect(toMap(ReviewState::getStateName, identity()));

    // creates the enum with the chosen type.
    ReviewState(long id, State backingState) {
        this.id = id;
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
        return assessmentReviewStateMap.keySet();
    }

    public static ReviewState getByState(String state) {
        return assessmentReviewStateMap.get(state);
    }

    public static ReviewState fromState(State state) {
        return ProcessState.fromState(values(), state);
    }

    public static Set<State> getBackingStates(Set<ReviewState> states) {
        return simpleMapSet(states, ReviewState::getBackingState);
    }

    @Override
    public long getId() {
        return id;
    }
}