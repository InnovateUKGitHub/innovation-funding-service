package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.workflow.resource.OutcomeType;

/**
 * Represents the events that can be triggered during the Project Setup process.
 */
public enum ProjectOutcomes implements OutcomeType {

    PROJECT_CREATED("project-created"),
    GOL_APPROVED("gol-approved");

    String event;

    ProjectOutcomes(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}

