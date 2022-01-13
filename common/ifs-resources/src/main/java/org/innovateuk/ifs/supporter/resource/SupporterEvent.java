package org.innovateuk.ifs.supporter.resource;


import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the assessment process workflow.
 */
public enum SupporterEvent implements ProcessEvent {
    CREATE("notify"),
    ACCEPT("accept"),
    REJECT("reject"),
    EDIT("edit");

    String event;

    SupporterEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
