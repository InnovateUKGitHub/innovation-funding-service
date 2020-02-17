package org.innovateuk.ifs.user.resource;

import com.google.common.base.Enums;

public enum RoleProfileState {

    ACTIVE("Available"),
    UNAVAILABLE("Unavailable"),
    DISABLED("Disabled");

    private String description;

    RoleProfileState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static RoleProfileState getRoleProfileStateFromString(String value) {
        return Enums.getIfPresent(RoleProfileState.class, value).or(RoleProfileState.ACTIVE);
    }
}
