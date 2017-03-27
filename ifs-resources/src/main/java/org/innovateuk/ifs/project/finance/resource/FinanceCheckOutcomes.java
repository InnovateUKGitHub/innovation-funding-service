package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.OutcomeType;

public enum FinanceCheckOutcomes implements OutcomeType {

    PROJECT_CREATED("project-created"),
    APPROVE("approved");

    String event;

    FinanceCheckOutcomes(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
