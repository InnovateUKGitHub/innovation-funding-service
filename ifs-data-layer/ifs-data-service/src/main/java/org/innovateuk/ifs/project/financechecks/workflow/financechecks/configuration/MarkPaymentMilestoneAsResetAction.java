package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneEvent;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneResetOutcome;
import org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneProcess;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class MarkPaymentMilestoneAsResetAction extends TestableTransitionWorkflowAction<PaymentMilestoneState, PaymentMilestoneEvent> {

    @Override
    protected final void doExecute(final StateContext<PaymentMilestoneState, PaymentMilestoneEvent> context) {
        PaymentMilestoneProcess viability = (PaymentMilestoneProcess) context.getMessageHeader("process");
        PaymentMilestoneResetOutcome outcome = (PaymentMilestoneResetOutcome) context.getMessageHeader("reset");
        if (outcome != null) {
            viability.getPaymentMilestoneResetOutcomes().add(outcome);
        }
    }
}