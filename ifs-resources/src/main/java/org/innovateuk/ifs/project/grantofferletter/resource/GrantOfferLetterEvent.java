package org.innovateuk.ifs.project.grantofferletter.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can be triggered during the Grant Offer Letter process.
 */
public enum GrantOfferLetterEvent implements ProcessEvent {

    PROJECT_CREATED("project-created"),
    GOL_REMOVED("gol-removed"),
    GOL_SENT("gol-sent"),
    GOL_SIGNED("gol-signed"),
    GOL_APPROVED("gol-approved"),
    GOL_REJECTED("gol-rejected");

    String event;

    GrantOfferLetterEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
