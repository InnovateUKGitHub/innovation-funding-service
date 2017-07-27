package org.innovateuk.ifs.assessment.panel.resource;

import org.innovateuk.ifs.workflow.resource.OutcomeType;

/**
 * Represents possible events that can happen during the AssessmentPanelApplicationProcess workflow.
 */
public enum AssessmentPanelApplicationInviteEvent implements OutcomeType {
    NOTIFY("notify"),
    ACCEPT("accept"),
    REJECT("reject");

    String event;

    AssessmentPanelApplicationInviteEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}