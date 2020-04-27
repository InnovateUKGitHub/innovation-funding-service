package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

public enum ApplicationEvent implements ProcessEvent {
    OPEN("opened"),
    APPROVE("approved"),
    SUBMIT("submitted"),
    REJECT("rejected"),
    MARK_INELIGIBLE("mark-ineligible"),
    INFORM_INELIGIBLE("inform-ineligible"),
    REINSTATE_INELIGIBLE("reinstate-ineligible"),
    WITHDRAW("withdraw-application"),
    UNSUBMIT("unsubmit-application");

    private final String event;

    ApplicationEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
