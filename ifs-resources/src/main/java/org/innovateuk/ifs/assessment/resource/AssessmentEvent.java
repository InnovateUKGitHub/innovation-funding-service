package org.innovateuk.ifs.assessment.resource;


import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the assessment process workflow.
 */
public enum AssessmentEvent implements ProcessEvent {
    NOTIFY("notify"),
    ACCEPT("accept"),
    REJECT("reject"),
    WITHDRAW("withdraw"),
    FEEDBACK("feedback"),
    FUNDING_DECISION("funding-decision"),
    SUBMIT("submit");

    String event;

    AssessmentEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
