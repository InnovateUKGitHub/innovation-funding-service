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

    SETUP(17, State.PENDING, 1),
    LIVE(18, State.ACCEPTED, 4),
    WITHDRAWN(48, State.WITHDRAWN, 5),
    HANDLED_OFFLINE(51, State.HANDLED_OFFLINE, 2),
    COMPLETED_OFFLINE(52, State.COMPLETED_OFFLINE, 6),
    ON_HOLD(53, State.ON_HOLD, 3),
    UNSUCCESSFUL(55 ,State.REJECTED, 7);

    private final long id;
    private final State backingState;
    private final int moDisplayOrder;

    ProjectState(long id, State backingState, int moDisplayOrder) {
        this.id = id;
        this.backingState = backingState;
        this.moDisplayOrder = moDisplayOrder;
    }

    @Override
    public String getStateName() {
        return backingState.name();
    }

    @Override
    public State getBackingState() {
        return backingState;
    }

    public int getMoDisplayOrder() {
        return moDisplayOrder;
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

    public boolean isUnsuccessful() {
        return this == UNSUCCESSFUL;
    }

    public boolean isOnHold() {
        return this == ON_HOLD;
    }

}