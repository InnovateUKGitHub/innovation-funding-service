package org.innovateuk.ifs.workflow.resource;

import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.workflow.resource.ProcessStates;

import java.time.ZonedDateTime;

/**
 * Base class for transferring basic information about a Process
 */
public abstract class BaseProcessResource<StateType extends ProcessStates, ParticipantType> {

    private StateType currentState;
    private ParticipantType participant;
    private UserResource internalParticipant;
    private ZonedDateTime modifiedDate;

    // for JSON marshalling
    protected BaseProcessResource() {

    }

    protected BaseProcessResource(StateType currentState, ParticipantType participant, UserResource internalParticipant, ZonedDateTime modifiedDate) {
        this.currentState = currentState;
        this.participant = participant;
        this.internalParticipant = internalParticipant;
        this.modifiedDate = modifiedDate;
    }

    public StateType getCurrentState() {
        return currentState;
    }

    public void setCurrentState(StateType currentState) {
        this.currentState = currentState;
    }

    public ParticipantType getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantType participant) {
        this.participant = participant;
    }

    public UserResource getInternalParticipant() {
        return internalParticipant;
    }

    public void setInternalParticipant(UserResource internalParticipant) {
        this.internalParticipant = internalParticipant;
    }

    public ZonedDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(ZonedDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
