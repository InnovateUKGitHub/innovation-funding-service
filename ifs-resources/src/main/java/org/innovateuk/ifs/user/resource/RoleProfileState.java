package org.innovateuk.ifs.user.resource;

public enum RoleProfileState {

    ACTIVE("Active"),
    UNAVAILABLE("Currently unavailable for assessment"),
    DISABLED("In our bad books for good");

    private String description;

    RoleProfileState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
