package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Represents the states that can be transitioned during the Project Setup process.
 */
public enum ProjectState implements ProcessState, IdentifiableEnum {

    SETUP(17, State.PENDING),
    LIVE(18, State.ACCEPTED),
    WITHDRAWN(48, State.WITHDRAWN),
    HANDLED_OFFLINE(51, State.HANDLED_OFFLINE),
    COMPLETED_OFFLINE(52, State.COMPLETED_OFFLINE),
    ON_HOLD(53, State.ON_HOLD),
    UNSUCCESSFUL(55 ,State.REJECTED);

    private final long id;
    private final State backingState;

    ProjectState(long id, State backingState) {
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

    public static List<State> getBackingStates() {
        return simpleMap(ProjectState.values(), ProcessState::getBackingState);
    }

    public static ProjectState fromState(State state) {
        return ProcessState.fromState(ProjectState.values(), state);
    }

    public static final Set<ProjectState> COMPLETED_STATES = EnumSet.of(LIVE, WITHDRAWN, COMPLETED_OFFLINE, UNSUCCESSFUL);

    @Override
    public long getId() {
        return id;
    }

    public boolean isOffline() {
        return this == COMPLETED_OFFLINE || this == HANDLED_OFFLINE;
    }

    public boolean isActive() {
        return this == SETUP || this == ON_HOLD;
    }

    public boolean isComplete() {
        return COMPLETED_STATES.contains(this);
    }

    public boolean isWithdrawn() {
        return this == WITHDRAWN;
    }

    public boolean isHandledOffline() {
        return this == HANDLED_OFFLINE;
    }

    public boolean isCompletedOffline() {
        return this == COMPLETED_OFFLINE;
    }

    public boolean isLive() {
        return this == LIVE;
    }

    public boolean isOnHold() {
        return this == ON_HOLD;
    }

    public boolean isUnsuccessful() {
        return this == UNSUCCESSFUL;
    }
}