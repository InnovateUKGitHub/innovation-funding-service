package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.OutcomeType;

/**
 * Represents the events that can be triggered during the Eligibility process.
 */
public enum EligibilityOutcomes implements OutcomeType {

    PROJECT_CREATED("project-created"),
    NOT_REQUESTING_FUNDING("not-requesting-funding"),
    ELIGIBILITY_APPROVED("eligibility-approved");

    String event;

    EligibilityOutcomes(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
