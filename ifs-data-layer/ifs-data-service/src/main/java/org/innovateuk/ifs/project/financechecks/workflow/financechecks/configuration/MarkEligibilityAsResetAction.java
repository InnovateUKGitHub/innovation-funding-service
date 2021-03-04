package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.EligibilityEvent;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityResetOutcome;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class MarkEligibilityAsResetAction extends TestableTransitionWorkflowAction<EligibilityState, EligibilityEvent> {

    @Override
    protected final void doExecute(final StateContext<EligibilityState, EligibilityEvent> context) {
        EligibilityProcess eligibility = (EligibilityProcess) context.getMessageHeader("process");
        EligibilityResetOutcome outcome = (EligibilityResetOutcome) context.getMessageHeader("reset");
        if (outcome != null) {
            eligibility.getEligibilityResetOutcomes().add(outcome);
        }
    }
}
