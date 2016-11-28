package com.worth.ifs.competition.resource;

import java.util.stream.Stream;

/**
 * This enum defines the milestones type for the competition
 */
public enum MilestoneType {
    OPEN_DATE("1. Open date"),
    BRIEFING_EVENT("2. Briefing event"),
    SUBMISSION_DATE("3. Submission date"),
    ALLOCATE_ASSESSORS("4. Allocate accessors"),
    ASSESSOR_BRIEFING("5. Assessor briefing"),
    ASSESSORS_NOTIFIED("Assessors notified", false),
    ASSESSOR_ACCEPTS("6. Assessor accepts"),
    ASSESSOR_DEADLINE("7. Assessor deadline"),
    ASSESSMENT_CLOSED("Assessment closed", false),
    LINE_DRAW("8. Line draw"),
    ASSESSMENT_PANEL("9. Assessment panel"),
    PANEL_DATE("10. Panel date"),
    FUNDERS_PANEL("11. Funders panel"),
    NOTIFICATIONS("12. Notifications"),
    RELEASE_FEEDBACK("13. Release feedback");

    private String milestoneDescription;
    private boolean presetDate;

    MilestoneType(String milestoneDescription) {
        this(milestoneDescription, true);
    }

    MilestoneType(String milestoneDescription, boolean presetDate) {
        this.milestoneDescription = milestoneDescription;
        this.presetDate = presetDate;
    }

    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    public boolean isPresetDate() {
        return presetDate;
    }

    public static MilestoneType[] presetValues() {
        return Stream.of(values()).filter(MilestoneType::isPresetDate).toArray(length -> new MilestoneType[length]);
    }
}

