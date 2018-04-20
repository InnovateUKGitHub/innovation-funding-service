package org.innovateuk.ifs.competition.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The current state of a Competition, or {@link CompetitionResource}
 */
public enum CompetitionStatus {
    COMPETITION_SETUP("Competition Setup",false, false),
    READY_TO_OPEN("Ready to open", true, false),
    OPEN("Open", true, false),
    CLOSED("Closed", true, false),
    IN_ASSESSMENT("In assessment", true, false),
    FUNDERS_PANEL("Panel", true, false),
    ASSESSOR_FEEDBACK("Inform", true, false),
    PROJECT_SETUP("Project setup",false, true);

    private String displayName;
    private boolean inFlight;
    private boolean feedbackReleased;

    CompetitionStatus(String displayName, boolean inFlight, boolean feedBackReleased) {
        this.displayName = displayName;
        this.inFlight = inFlight;
        this.feedbackReleased = feedBackReleased;
    }

    public static final ImmutableSet<CompetitionStatus> fundingNotCompleteStatuses = Sets.immutableEnumSet(
            OPEN,
            CLOSED,
            IN_ASSESSMENT,
            FUNDERS_PANEL
    );

    public static final ImmutableSet<CompetitionStatus> fundingCompleteStatuses = Sets.immutableEnumSet(
            ASSESSOR_FEEDBACK,
            PROJECT_SETUP
    );

    public String getDisplayName() {
        return displayName;
    }

    public boolean isInFlight() {
        return inFlight;
    }

    public boolean isFeedbackReleased() {
        return feedbackReleased;
    }

    public boolean isLaterThan(CompetitionStatus status) {
        return this.ordinal() > status.ordinal();
    }
}
