package com.worth.ifs.invite.domain;

import com.worth.ifs.user.domain.User;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Optional;

/**
 * A Participant in a {@link ProcessActivity}.
 */
public abstract class Participant<P extends ProcessActivity, I extends Invite<P,I>, R extends ParticipantRole<P>> {
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    protected Participant() {
        this.status = ParticipantStatus.PENDING;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    public abstract P getProcess();

    public abstract Optional<I> getInvite();

    public abstract R getRole();

    // TODO make this Optional<User>
    public abstract User getUser();

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
