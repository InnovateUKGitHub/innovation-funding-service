package org.innovateuk.ifs.assessment.resource;

/**
 * The possible outcome values for an {@code AssessmentRejectOutcome}.
 */
public enum AssessmentRejectOutcomeValue {

    CONFLICT_OF_INTEREST("Conflict of interest"),
    NOT_AREA_OF_EXPERTISE("Not my area of expertise"),
    TOO_MANY_ASSESSMENTS("Too many assessments");

    String displayLabel;

    AssessmentRejectOutcomeValue(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}