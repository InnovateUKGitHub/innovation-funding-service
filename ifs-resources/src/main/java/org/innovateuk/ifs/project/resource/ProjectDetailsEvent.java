package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

public enum ProjectDetailsEvent implements ProcessEvent {

    PROJECT_CREATED("project-created"),
    PROJECT_START_DATE_ADDED("start-date-added"),
    PROJECT_ADDRESS_ADDED("address-added"),
    PROJECT_MANAGER_ADDED("project-manager-added"),
    SUBMIT("submitted");

    String event;

    ProjectDetailsEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
