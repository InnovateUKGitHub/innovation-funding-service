package org.innovateuk.ifs.sil;

public enum SIlPayloadType {
    APPLICATION_SUBMISSION("Application submission"),
    CONTACT("Contact sync created / updated"),
    ASSESSMENT_COMPLETE("Assessment completed"),
    APPLICATION_UPDATE("Update application state"),
    USER_UPDATE("User profile updated");


    private final String displayName;

    SIlPayloadType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
