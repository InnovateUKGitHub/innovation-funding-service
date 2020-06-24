package org.innovateuk.ifs.competition.resource;

/**
 * An enum representing the stage at which a Competition is deemed complete.
 */
public enum CompetitionCompletionStage {

    COMPETITION_CLOSE("Competition Close", MilestoneType.SUBMISSION_DATE),
    RELEASE_FEEDBACK("Release feedback", MilestoneType.FEEDBACK_RELEASED),
    PROJECT_SETUP("Project setup", MilestoneType.FEEDBACK_RELEASED);

    private String displayName;
    private MilestoneType lastMilestone;

    CompetitionCompletionStage(String displayName, MilestoneType lastMilestone) {
        this.displayName = displayName;
        this.lastMilestone = lastMilestone;
    }

    public String getDisplayName() {
        return displayName;
    }

    public MilestoneType getLastMilestone() {
        return lastMilestone;
    }
}
