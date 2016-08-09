package com.worth.ifs.invite.domain;

/**
 * The status of a {@link Participant}.
 *
 * Legal transitions are:
 *  () -> PENDING
 *  PENDING -> ACCEPTED
 *  PENDING -> REJECTED
 *
 *  @see Participant#setStatus(ParticipantStatus)
 */
public enum ParticipantStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
