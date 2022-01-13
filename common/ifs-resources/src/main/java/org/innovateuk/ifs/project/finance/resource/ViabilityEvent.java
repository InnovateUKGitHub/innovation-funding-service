package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can be triggered during the Viability Approval process.
 */
public enum ViabilityEvent implements ProcessEvent {

    PROJECT_CREATED("project-created"),
    VIABILITY_NOT_APPLICABLE("viability-not-applicable"),  // currently used for academic organisations and h2020 users
    VIABILITY_APPROVED("viability-approved"),
    VIABILITY_RESET("viability-reset");

    String event;

    ViabilityEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
