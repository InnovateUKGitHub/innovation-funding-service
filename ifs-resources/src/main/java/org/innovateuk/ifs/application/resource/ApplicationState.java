package org.innovateuk.ifs.application.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum ApplicationState implements ProcessState, IdentifiableEnum<ApplicationState> {

    CREATED(27, State.CREATED), // initial state
    SUBMITTED(29,State.SUBMITTED),
    INELIGIBLE(30, State.NOT_APPLICABLE),
    INELIGIBLE_INFORMED(31, State.NOT_APPLICABLE_INFORMED),
    APPROVED(32, State.ACCEPTED),
    REJECTED(33, State.REJECTED),
    OPEN(28, State.OPEN),
    WITHDRAWN(49, State.WITHDRAWN);

    final long id;
    final State backingState;

    public static final ImmutableSet<ApplicationState> submittedAndFinishedStates = Sets.immutableEnumSet(
            SUBMITTED,
            INELIGIBLE,
            APPROVED,
            REJECTED,
            INELIGIBLE_INFORMED,
            WITHDRAWN);

    public static final ImmutableSet<ApplicationState> inProgressStates = Sets.immutableEnumSet(
            CREATED,
            OPEN);

    public static final ImmutableSet<ApplicationState> finishedStates = Sets.immutableEnumSet(
            APPROVED,
            REJECTED,
            INELIGIBLE_INFORMED,
            WITHDRAWN
    );

    public static final ImmutableSet<ApplicationState> submittedStates = Sets.immutableEnumSet(
            SUBMITTED,
            INELIGIBLE
    );

    public static final ImmutableSet<ApplicationState> unsuccessfulStates = Sets.immutableEnumSet(
            INELIGIBLE,
            INELIGIBLE_INFORMED,
            REJECTED,
            WITHDRAWN
    );

    ApplicationState(final long id, final State backingState) {
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

    @Override
    public long getId() {
        return id;
    }

    public static List<State> getBackingStates() {
        return simpleMap(ApplicationState.values(), ProcessState::getBackingState);
    }

    public static ApplicationState fromState(State state) {
        return ProcessState.fromState(ApplicationState.values(), state);
    }
}