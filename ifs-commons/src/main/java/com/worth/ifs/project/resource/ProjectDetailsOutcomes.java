package com.worth.ifs.project.resource;

import com.worth.ifs.workflow.resource.OutcomeType;

public enum ProjectDetailsOutcomes implements OutcomeType {

    PENDING("pending"),
    PROJECT_CREATED("pending"),
    PROJECT_START_DATE_ADDED("start-date-added"),
    PROJECT_ADDRESS_ADDED("address-added"),
    PROJECT_MANAGER_ADDED("project-manager-added"),
    PROJECT_FINANCE_CONTACT_ADDED("finance-contact-added"),
    SUBMIT("submitted");

    String event;

    ProjectDetailsOutcomes(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
