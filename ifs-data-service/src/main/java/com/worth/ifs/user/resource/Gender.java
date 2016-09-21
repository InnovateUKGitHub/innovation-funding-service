package com.worth.ifs.user.resource;

import com.worth.ifs.user.domain.User;

/**
 * The gender of a {@link User}.
 */
public enum Gender {
    FEMALE("Female"),
    MALE("Male"),
    NOT_STATED("Prefer not to say");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
