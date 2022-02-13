package org.innovateuk.ifs.user.resource;

/**
 * Java enumeration of the EDI statuses.
 * The value of these entries are used when saving to and interpreting from the database.
 */
public enum EDIStatus {
    COMPLETE("Complete"),
    INCOMPLETE("Incomplete"),
    INPROGRESS("In Progress");

    private String displayName;

    EDIStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
