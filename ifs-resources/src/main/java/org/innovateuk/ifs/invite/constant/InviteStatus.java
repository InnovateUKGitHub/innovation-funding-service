package org.innovateuk.ifs.invite.constant;

/**
 * Java enumeration of the invite statuses.
 * The value of these entries are used when saving to and interpreting from the database.
 */
public enum InviteStatus {
    SENT("Sent"),
    CREATED("Not sent"),
    OPENED("Opened");

    private String displayName;

    InviteStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
