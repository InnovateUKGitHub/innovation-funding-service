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
    LINE_DRAW("8. Line draw"),
    ASSESSMENT_PANEL("9. Assessment panel"),
    PANEL_DATE("10. Panel date"),
    FUNDERS_PANEL("11. Funders panel"),
    NOTIFICATIONS("12. Notifications"),
    RELEASE_FEEDBACK("13. Release feedback");

    private String milestoneDescription;

    MilestoneType(String milestoneDescription) {
        this.milestoneDescription = milestoneDescription;
    }

    public String getMilestoneDescription() {
        return milestoneDescription;
    }
}

