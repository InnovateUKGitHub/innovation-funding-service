package org.innovateuk.ifs.assessment.interview.resource;


import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the assessment interview panel process workflow.
 */
public enum AssessmentInterviewPanelEvent implements ProcessEvent {
    NOTIFY("notify"),
    RESPOND("respond");

    String event;

    AssessmentInterviewPanelEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}