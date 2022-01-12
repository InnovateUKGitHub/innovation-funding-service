package org.innovateuk.ifs.interview.resource;


import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the assessment interview panel process workflow.
 */
public enum InterviewAssignmentEvent implements ProcessEvent {
    NOTIFY("notify"),
    RESPOND("respond"),
    WITHDRAW_RESPONSE("withdraw-response");

    String event;

    InterviewAssignmentEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}