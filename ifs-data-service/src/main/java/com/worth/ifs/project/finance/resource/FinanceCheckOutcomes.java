package com.worth.ifs.project.finance.resource;

import com.worth.ifs.workflow.resource.OutcomeType;

public enum FinanceCheckOutcomes implements OutcomeType {

    PENDING("pending"),
    PROJECT_CREATED("pending"),
    FINANCE_CHECK_FIGURES_EDITED("finance-check-figures-edited"),
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
