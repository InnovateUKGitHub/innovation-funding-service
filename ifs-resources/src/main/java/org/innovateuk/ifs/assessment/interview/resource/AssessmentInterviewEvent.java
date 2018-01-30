package org.innovateuk.ifs.assessment.interview.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the AssessmentInterview workflow.
 */
public enum AssessmentInterviewEvent implements ProcessEvent {
    NOTIFY("notify"),
    ACCEPT("accept"),
    REJECT("reject"),
    WITHDRAW("withdraw");

    String event;

    AssessmentInterviewEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}