package org.innovateuk.ifs.sil;

public enum SIlPayloadType {
    APPLICATION_SUBMISSION("Verified"),
    CONTACT("Not Verified"),
    ASSESSMENT_COMPLETE("Created but not registered or invited"),
    APPLICATION_UPDATE("Created but not registered or invited"),
    USER_UPDATE("Created but not registered or invited");


    private String displayName;

    SIlPayloadType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
