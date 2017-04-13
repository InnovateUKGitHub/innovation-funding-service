package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.workflow.resource.OutcomeType;

// TODO are these really 'outcomes', they feel more like events, or actions?
public enum ApplicationOutcome implements OutcomeType {
    OPENED("opened"),
    APPROVED("approved"),
    SUBMITTED("submitted"),
    REJECTED("rejected");

    private final String event;

    ApplicationOutcome(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
