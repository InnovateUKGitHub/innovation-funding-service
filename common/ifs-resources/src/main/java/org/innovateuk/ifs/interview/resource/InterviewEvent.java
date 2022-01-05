package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the AssessmentInterview workflow.
 */
public enum InterviewEvent implements ProcessEvent {
    NOTIFY("notify");

    String event;

    InterviewEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}