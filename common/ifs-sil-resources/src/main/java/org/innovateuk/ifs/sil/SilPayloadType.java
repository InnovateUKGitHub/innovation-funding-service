package org.innovateuk.ifs.sil;

public enum SilPayloadType {
    APPLICATION_SUBMISSION("Application submit"),
    CONTACT("Contact sync created / update"),
    ASSESSMENT_COMPLETE("Assessment complete"),
    APPLICATION_UPDATE("Update application state"),
    USER_UPDATE("User profile update");


    private final String displayName;

    SilPayloadType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
