package org.innovateuk.ifs.user.resource;

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
}
