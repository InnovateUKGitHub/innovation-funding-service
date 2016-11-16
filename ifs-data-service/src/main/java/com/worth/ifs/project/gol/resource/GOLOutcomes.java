package com.worth.ifs.project.gol.resource;

import com.worth.ifs.workflow.resource.OutcomeType;

public enum GOLOutcomes implements OutcomeType {

    PENDING("pending"), // TODO - Don't think we need this as this is not an event. Duncan - please let me know

    PROJECT_CREATED("project-created"),
    GOL_SENT("gol-sent"),
    GOL_SIGNED("gol-signed"),
    GOL_APPROVED("gol-approved"),
    GOL_REJECTED("gol-rejected");

    String event;

    GOLOutcomes(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
