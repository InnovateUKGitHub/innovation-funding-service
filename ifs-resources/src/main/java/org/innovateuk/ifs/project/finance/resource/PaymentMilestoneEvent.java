package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can be triggered during the {@link org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneProcess} process.
 */
public enum PaymentMilestoneEvent implements ProcessEvent {

    PROJECT_CREATED("project-created"),
    PAYMENT_MILESTONE_APPROVED("payment-milestones-approved"),
    PAYMENT_MILESTONE_RESET("payment-milestones-reset");

    String event;

    PaymentMilestoneEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
