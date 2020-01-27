package org.innovateuk.ifs.user.resource;

public enum RoleProfileState {

    ACTIVE("Active"),
    UNAVAILABLE("Temporarily unavailable"),
    DISABLED("Permanently unavailable");

    private String description;

    RoleProfileState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
