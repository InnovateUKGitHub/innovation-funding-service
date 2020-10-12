package org.innovateuk.ifs.cofunder.resource;


import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the assessment process workflow.
 */
public enum CofunderEvent implements ProcessEvent {
    CREATE("notify"),
    ACCEPT("accept"),
    REJECT("reject"),
    EDIT("edit");

    String event;

    CofunderEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
