package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.OutcomeType;

/**
 * Represents the events that can be triggered during the Viability Approval process.
 */
public enum ViabilityOutcomes implements OutcomeType {

    PROJECT_CREATED("project-created"),
    IS_ACADEMIC_ORGANISATION("is-academic-organisation"),
    VIABILITY_APPROVED("viability-approved");

    String event;

    ViabilityOutcomes(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
