package org.innovateuk.ifs.sil;

public enum SIlPayloadKeyType {
    COMPETITION_ID("Competition ID"),
    APPLICATION_ID("Application ID"),
    USER_ID("User ID ");


    private final String displayName;

    SIlPayloadKeyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
