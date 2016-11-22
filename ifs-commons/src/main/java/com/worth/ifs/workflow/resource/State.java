package com.worth.ifs.workflow.resource;

/**
 * Represents a set of possible states that a given ActivityType can be in
 */
public enum State {

    PENDING,
    REJECTED,
    ACCEPTED,
    OPEN,
    DECIDE_IF_READY_TO_SUBMIT,
    READY_TO_SUBMIT,
    SUBMITTED,
    VERIFIED,
    NOT_VERIFIED,
    ASSIGNED,
    NOT_ASSIGNED
}
