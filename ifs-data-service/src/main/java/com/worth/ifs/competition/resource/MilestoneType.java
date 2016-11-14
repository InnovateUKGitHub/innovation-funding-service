package com.worth.ifs.competition.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This enum defines the milestones type for the competition
 */
public enum MilestoneType {
    OPEN_DATE("1. Open date"),
    BRIEFING_EVENT("2. Briefing event"),
    SUBMISSION_DATE("3. Submission date"),
    ALLOCATE_ASSESSORS("4. Allocate accessors"),
    ASSESSOR_BRIEFING("5. Assessor briefing"),
    ASSESSOR_ACCEPTS("6. Assessor accepts"),
    ASSESSOR_DEADLINE("7. Assessor deadline"),
    ASSESSORS_NOTIFIED("8. Assessors Notified", false), // terminology varies, but this corresponds to the button in Assessor Management
    ASSESSMENT_CLOSED("9. Assessment closed", false),
    LINE_DRAW("10. Line draw"),
    ASSESSMENT_PANEL("11. Assessment panel"),
    PANEL_DATE("12. Panel date"),
    FUNDERS_PANEL("13. Funders panel"),
    NOTIFICATIONS("14. Notifications"),
    RELEASE_FEEDBACK("15. Release feedback");

    private final String milestoneDescription;
    private final boolean dateMandatory;

    private MilestoneType(String milestoneDescription) {
        this(milestoneDescription, false);
    }

    private MilestoneType(String milestoneDescription, boolean dateMandatory) {
        this.milestoneDescription = milestoneDescription;
        this.dateMandatory = dateMandatory;
    }

    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    public boolean isDateMandatory() {
        return dateMandatory;
    }
}

