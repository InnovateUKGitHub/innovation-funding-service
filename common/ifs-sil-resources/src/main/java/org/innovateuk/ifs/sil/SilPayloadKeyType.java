package org.innovateuk.ifs.sil;

public enum SilPayloadKeyType {
    COMPETITION_ID("Competition ID"),
    APPLICATION_ID("Application ID"),
    USER_ID("User ID ");


    private final String displayName;

    SilPayloadKeyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
