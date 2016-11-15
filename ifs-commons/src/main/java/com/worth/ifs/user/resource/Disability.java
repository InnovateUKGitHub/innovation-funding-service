package com.worth.ifs.user.resource;


/**
 * The disabilities of an User.
 */
public enum Disability {
    YES(1L, "Yes"),
    NO(2L, "No"),
    NOT_STATED(3L, "Prefer not to say");

    private String displayName;
    private Long id;

    Disability(Long id, String displayName) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getId() {
        return id;
    }
}
