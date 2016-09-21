package com.worth.ifs.user.resource;

import com.worth.ifs.user.domain.User;

/**
 * The disabilities of a {@link User}.
 */
public enum Disability {
    YES("Yes"),
    NO("No"),
    NOT_STATED("Prefer not to say");

    private final String displayName;

    Disability(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
