package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.util.enums.Identifiable;

/**
 * The status of a {@link Participant}.
 *
 * Legal transitions are:
 *
 * <blockquote><pre>
 *  () -> PENDING
 *  PENDING -> ACCEPTED
 *  PENDING -> REJECTED
 * </pre></blockquote>
 *
 * @see Participant#setStatus(ParticipantStatus)
 */
public enum ParticipantStatus implements Identifiable {
    PENDING(1),
    ACCEPTED(2),
    REJECTED(3);

    private final long id;

    ParticipantStatus(final long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }
}
