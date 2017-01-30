package org.innovateuk.ifs.competition.resource;

/**
 * The current state of a Competition, or {@link CompetitionResource}
 */
public enum CompetitionStatus {
    COMPETITION_SETUP("Competition Setup",false),
    READY_TO_OPEN("Ready to open", true),
    OPEN("Open", true),
    CLOSED("Closed", true),
    IN_ASSESSMENT("In assessment", true),
    FUNDERS_PANEL("Panel", true),
    ASSESSOR_FEEDBACK("Inform", true),
    PROJECT_SETUP("Project setup",false);

    private String displayName;
    private boolean inFlight;

    CompetitionStatus(String displayName, boolean inFlight) {
        this.displayName = displayName;
        this.inFlight = inFlight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isInFlight() {
        return inFlight;
    }
}
