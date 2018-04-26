package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can be triggered during the Project Setup process.
 */
public enum ProjectEvent implements ProcessEvent {

    PROJECT_CREATED("project-created"),
    GOL_APPROVED("signed-gol-approved"),
    PROJECT_WITHDRAWN("withdraw-project");

    String event;

    ProjectEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}

