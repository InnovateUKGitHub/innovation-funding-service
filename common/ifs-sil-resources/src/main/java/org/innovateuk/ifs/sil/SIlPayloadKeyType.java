package org.innovateuk.ifs.sil;

public enum SIlPayloadKeyType {
    COMPETITION_ID("Verified"),
    APPLICATION_ID("Not Verified"),
    USER_ID("Created but not registered or invited");


    private String displayName;

    SIlPayloadKeyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
