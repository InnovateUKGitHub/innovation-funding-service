package com.worth.ifs.workflow.resource;

/**
 * Represents a set of possible states that a given ActivityType can be in
 */
public enum State {

    OPEN,
    PENDING,
    READY_TO_SUBMIT,
    SUBMITTED,
    ACCEPTED,
    REJECTED,
    VERIFIED,
    NOT_VERIFIED,
    ASSIGNED,
    NOT_ASSIGNED
}
