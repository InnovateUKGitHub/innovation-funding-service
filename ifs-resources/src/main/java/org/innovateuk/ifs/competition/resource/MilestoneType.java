package org.innovateuk.ifs.competition.resource;

import java.util.EnumSet;
import java.util.stream.Stream;

/**
 * This enum defines the milestones type for the competition
 */
public enum MilestoneType {
    OPEN_DATE("1. Open date", false, 1),
    BRIEFING_EVENT("2. Briefing event", false, 2),
    REGISTRATION_DATE("Registration date", true, 3),
    SUBMISSION_DATE("3. Submission date", false, 4),
    ALLOCATE_ASSESSORS("4. Allocate assessors", false, 5),
    ASSESSOR_BRIEFING("5. Assessor briefing", false, 6),
    ASSESSORS_NOTIFIED("Assessors notified", false, false, 7),
    ASSESSOR_ACCEPTS("6. Assessor accepts", false, 8),
    ASSESSOR_DEADLINE("7. Assessor deadline", false, 9),
    ASSESSMENT_CLOSED("Assessment closed", false, false, 10),
    LINE_DRAW("8. Line draw", false, 11),
    ASSESSMENT_PANEL("9. Assessment panel", false, 12),
    PANEL_DATE("10. Panel date", false, 13),
    FUNDERS_PANEL("11. Funders panel", false, 14),
    NOTIFICATIONS("12. Notifications", false, 15),
    RELEASE_FEEDBACK("13. Release feedback", false,  16),
    FEEDBACK_RELEASED("Feedback released", false, false, 17);

    private String milestoneDescription;
    private boolean presetDate;
    private boolean onlyNonIfs;
    private int priority;

    MilestoneType(String milestoneDescription, boolean onlyNonIfs, int priority) {
        this(milestoneDescription, true, onlyNonIfs, priority);
    }

    MilestoneType(String milestoneDescription, boolean presetDate, boolean onlyNonIfs, int priority) {
        this.milestoneDescription = milestoneDescription;
        this.presetDate = presetDate;
        this.onlyNonIfs = onlyNonIfs;
        this.priority = priority;
    }

    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    public String getAlwaysOpenDescription() {
        String milestoneDescription = getMilestoneDescription();
        if (this == SUBMISSION_DATE) {
            return getMilestoneDescription().replaceAll("[0-9]\\.", "2.");
        } else if (this == ASSESSOR_BRIEFING) {
            return getMilestoneDescription().replaceAll("[0-9]\\.", "1.");
        } else if (this == ASSESSOR_ACCEPTS) {
            return getMilestoneDescription().replaceAll("[0-9]\\.", "2.");
        } else if (this == ASSESSOR_DEADLINE) {
            return getMilestoneDescription().replaceAll("[0-9]\\.", "3.");
        }
        return milestoneDescription;
    }

    public boolean isOnlyNonIfs() {
        return onlyNonIfs;
    }

    public boolean isPresetDate() {
        return presetDate;
    }

    public int getPriority() {
        return priority;
    }

    public static MilestoneType[] presetValues() {
        return Stream.of(values()).filter(MilestoneType::isPresetDate).toArray(length -> new MilestoneType[length]);
    }

    public static EnumSet<MilestoneType> alwaysOpenCompSetupMilestones() {
        return EnumSet.of(OPEN_DATE, SUBMISSION_DATE);
    }

    public static EnumSet<MilestoneType> assessmentPeriodValues() {
        return EnumSet.of(ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE, ASSESSORS_NOTIFIED, ASSESSMENT_CLOSED);
    }
}

