package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can be triggered during the Eligibility process.
 */
public enum EligibilityEvent implements ProcessEvent {

    PROJECT_CREATED("project-created"),
    NOT_REQUESTING_FUNDING("not-requesting-funding"),
    ELIGIBILITY_APPROVED("eligibility-approved");

    String event;

    EligibilityEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
