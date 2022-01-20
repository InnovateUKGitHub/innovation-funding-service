package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

public enum FundingRulesEvent implements ProcessEvent {

    PROJECT_CREATED("project-created"),
    FUNDING_RULES_UPDATED("funding-rules-updated"),
    FUNDING_RULES_APPROVED("funding-rules-approved");

    String event;

    FundingRulesEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
