package com.worth.ifs.invite.constant;

/**
 * Java enumeration of the invite statuses.
 * The value of these entries are used when saving to and interpreting from the database.
 */
public enum InviteStatus {
    SENT(1, "SENT"),
    CREATED(2, "CREATED"),
    OPENED(3, "OPENED");

    private long id;
    private String name;

    InviteStatus(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
