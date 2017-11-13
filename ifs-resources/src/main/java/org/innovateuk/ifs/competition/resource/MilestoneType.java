package org.innovateuk.ifs.competition.resource;

import java.util.stream.Stream;

/**
 * This enum defines the milestones type for the competition
 */
public enum MilestoneType {
    OPEN_DATE("1. Open date", false),
    BRIEFING_EVENT("2. Briefing event", false),
    REGISTRATION_DATE("Registration date", true),
    SUBMISSION_DATE("3. Submission date", false),
    ALLOCATE_ASSESSORS("4. Allocate assessors", false),
    ASSESSOR_BRIEFING("5. Assessor briefing", false),
    ASSESSORS_NOTIFIED("Assessors notified", false, false),
    ASSESSOR_ACCEPTS("6. Assessor accepts", false),
    ASSESSOR_DEADLINE("7. Assessor deadline", false),
    ASSESSMENT_CLOSED("Assessment closed", false, false),
    LINE_DRAW("8. Line draw", false),
    ASSESSMENT_PANEL("9. Assessment panel", false),
    PANEL_DATE("10. Panel date", false),
    FUNDERS_PANEL("11. Funders panel", false),
    NOTIFICATIONS("12. Notifications", false),
    RELEASE_FEEDBACK("13. Release feedback", false),
    FEEDBACK_RELEASED("Feedback released", false, false);

    private String milestoneDescription;
    private boolean presetDate;
    private boolean onlyNonIfs;

    MilestoneType(String milestoneDescription, boolean onlyNonIfs) {
        this(milestoneDescription, true, onlyNonIfs);
    }

    MilestoneType(String milestoneDescription, boolean presetDate, boolean onlyNonIfs) {
        this.milestoneDescription = milestoneDescription;
        this.presetDate = presetDate;
        this.onlyNonIfs = onlyNonIfs;
    }

    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    public boolean isOnlyNonIfs() {
        return onlyNonIfs;
    }

    public boolean isPresetDate() {
        return presetDate;
    }

    public static MilestoneType[] presetValues() {
        return Stream.of(values()).filter(MilestoneType::isPresetDate).toArray(length -> new MilestoneType[length]);
    }
}

