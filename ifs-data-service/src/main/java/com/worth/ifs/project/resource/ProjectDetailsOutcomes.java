package com.worth.ifs.project.resource;

import com.worth.ifs.workflow.resource.OutcomeType;

public enum ProjectDetailsOutcomes implements OutcomeType {

    PENDING("pending"),
    PROJECT_CREATED("pending"),
    PROJECT_START_DATE_ADDED("mandatory-value-added"),
    PROJECT_ADDRESS_ADDED("mandatory-value-added"),
    PROJECT_MANAGER_ADDED("mandatory-value-added"),
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
