package org.innovateuk.ifs.workflow.resource;

/**
 * Represents a set of possible states that a given ActivityType can be in
 */
public enum State {

    CREATED,
    PENDING,
    WITHDRAWN,
    REJECTED,
    ACCEPTED,
    OPEN,
    DECIDE_IF_READY_TO_SUBMIT,
    READY_TO_SUBMIT,
    SUBMITTED,
    VERIFIED,
    NOT_VERIFIED,
    ASSIGNED,
    NOT_ASSIGNED,
    NOT_APPLICABLE,
    NOT_APPLICABLE_INFORMED;
}
