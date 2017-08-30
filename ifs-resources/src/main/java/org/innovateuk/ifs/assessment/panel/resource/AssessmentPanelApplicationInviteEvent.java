package org.innovateuk.ifs.assessment.panel.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the AssessmentPanelApplicationProcess workflow.
 */
public enum AssessmentPanelApplicationInviteEvent implements ProcessEvent {
    NOTIFY("notify"),
    ACCEPT("accept"),
    REJECT("reject"),
    MARK_CONFLICT_OF_INTEREST("mark_conflict_of_interest"),
    UNMARK_CONFLICT_OF_INTEREST("unmark_conflict_of_interest");

    String event;

    AssessmentPanelApplicationInviteEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}