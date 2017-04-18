package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.workflow.resource.OutcomeType;

public enum ApplicationOutcome implements OutcomeType {
    OPENED("opened"),
    APPROVED("approved"),
    SUBMITTED("submitted"),
    REJECTED("rejected"),
    MARK_INELIGIBLE("mark-ineligible"),
    INFORM_INELIGIBLE("inform-ineligible"),
    REINSTATE_INELIGIBLE("reinstate-ineligible");

    private final String event;

    ApplicationOutcome(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
