package org.innovateuk.ifs.competition.resource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An enum representing the stage at which a Competition is deemed complete.
 */
public enum CompetitionCompletionStage {

    COMPETITION_CLOSE("Competition close", MilestoneType.SUBMISSION_DATE),
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

    public static List<CompetitionCompletionStage> alwaysOpenValues() {
        return getCompetitionCompletionStages();
    }

    public static List<CompetitionCompletionStage> assessmentStageOpenValues() {
        return getCompetitionCompletionStages();
    }

    private static List<CompetitionCompletionStage> getCompetitionCompletionStages() {
        return Stream.of(values())
                .filter(completionStage -> (completionStage == RELEASE_FEEDBACK || completionStage == PROJECT_SETUP))
                .collect(Collectors.toList());
    }

}
