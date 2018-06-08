package org.innovateuk.ifs.application.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum ApplicationState implements ProcessState, IdentifiableEnum<ApplicationState> {

    CREATED(27, State.CREATED, "Started"), // initial state
    SUBMITTED(29,State.SUBMITTED, "Submitted"),
    INELIGIBLE(30, State.NOT_APPLICABLE, "Ineligible"),
    INELIGIBLE_INFORMED(31, State.NOT_APPLICABLE_INFORMED, "Ineligible"),
    APPROVED(32, State.ACCEPTED, "Successful"),
    REJECTED(33, State.REJECTED, "Unsuccessful"),
    OPEN(28, State.OPEN, "In progress"),
    WITHDRAWN(49, State.WITHDRAWN, "Withdrawn");

    final long id;
    final State backingState;
    private String displayName;

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

    public static final ImmutableSet<ApplicationState> ineligibleStates = Sets.immutableEnumSet(
            INELIGIBLE,
            INELIGIBLE_INFORMED
    );

    ApplicationState(final long id, final State backingState, String displayName) {
        this.id = id;
        this.backingState = backingState;
        this.displayName = displayName;
    }

    @Override
    public String getStateName() {
        return backingState.name();
    }

    @Override
    public State getBackingState() {
        return backingState;
    }

    public String getDisplayName() {
        return displayName;
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
