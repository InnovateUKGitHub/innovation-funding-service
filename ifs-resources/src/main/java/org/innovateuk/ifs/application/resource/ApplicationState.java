package org.innovateuk.ifs.application.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.util.enums.Identifiable;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum ApplicationState implements ProcessState, Identifiable {
    // TODO ids are probably wrong

    CREATED(27, State.CREATED), // initial state
    SUBMITTED(28,State.SUBMITTED),
    INELIGIBLE(29, State.NOT_APPLICABLE),
    INELIGIBLE_INFORMED(30, State.NOT_APPLICABLE_INFORMED),
    APPROVED(31, State.ACCEPTED),
    REJECTED(32, State.REJECTED),
    OPEN(33, State.OPEN),
    IN_PANEL(34, State.IN_PANEL);

    final long id;
    final State backingState;

    public static final ImmutableSet<ApplicationState> submittedAndFinishedStates = Sets.immutableEnumSet(
            SUBMITTED,
            INELIGIBLE,
            APPROVED,
            REJECTED,
            INELIGIBLE_INFORMED);

    public static final ImmutableSet<ApplicationState> inProgressStates = Sets.immutableEnumSet(
            CREATED,
            OPEN);

    public static final ImmutableSet<ApplicationState> finishedStates = Sets.immutableEnumSet(
            APPROVED,
            REJECTED,
            INELIGIBLE_INFORMED
    );

    public static final ImmutableSet<ApplicationState> submittedStates = Sets.immutableEnumSet(
            SUBMITTED,
            INELIGIBLE
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
