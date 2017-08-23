package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can be triggered during the Viability Approval process.
 */
public enum ViabilityEvent implements ProcessEvent {

    PROJECT_CREATED("project-created"),
    ORGANISATION_IS_ACADEMIC("organisation-is-academic"),
    VIABILITY_APPROVED("viability-approved");

    String event;

    ViabilityEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
