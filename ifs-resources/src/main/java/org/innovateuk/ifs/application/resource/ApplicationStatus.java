package org.innovateuk.ifs.application.resource;


/**
 * Java enumeration of the current available Application workflow statuses.
 * The value of these entries are used when saving to the database.
 */
public enum ApplicationStatus {
    CREATED, // initial state
    SUBMITTED,
    APPROVED,
    REJECTED,
    OPEN
}
