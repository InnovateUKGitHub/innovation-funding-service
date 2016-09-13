package com.worth.ifs.project.resource;

import com.worth.ifs.workflow.resource.OutcomeType;

public enum ProjectDetailsOutcomes implements OutcomeType {

    READY_TO_SUBMIT("ready-to-submit"),
    SUBMIT("submit");

    String event;

    ProjectDetailsOutcomes(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
