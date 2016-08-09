package com.worth.ifs.invite.domain;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Participant in a {@link ProcessActivity}
 */
public abstract class Participant<T extends ProcessActivity, I extends Invite<T,I>> {

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    protected Participant() {
        this.status = ParticipantStatus.PENDING;
    }

    public abstract T getProcess();

    public abstract I getInvite();

    public ParticipantStatus getStatus() {
        return status;
    }

    protected void setStatus(ParticipantStatus newStatus) {
        switch (newStatus) {
            case PENDING:
                if (this.status != null) throw new IllegalStateException("cannot change an existing Participant to PENDING");
                break;
            case ACCEPTED:
            case REJECTED:
                if (this.status != ParticipantStatus.PENDING)
                    throw new IllegalStateException("cannot change a " + this.status + "  Participant to " + newStatus);
            break;
        }

        this.status = newStatus;
    }
}
