package org.innovateuk.ifs.user.resource;

public enum UserStatus {
    ACTIVE("Verified"),
    INACTIVE("Not Verified");

    private String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
