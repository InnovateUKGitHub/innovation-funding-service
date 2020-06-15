package org.innovateuk.ifs.competition.resource;

/**
 * An enum representing the stage at which a Competition is deemed complete.
 */
public enum CompetitionCompletionStage {

    COMPETITION_CLOSE("Competition Close"),
    RELEASE_FEEDBACK("Release feedback"),
    PROJECT_SETUP("Project setup");

    private String displayName;

    CompetitionCompletionStage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
