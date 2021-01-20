package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.ViabilityEvent;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityResetOutcome;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class MarkViabilityAsResetAction extends TestableTransitionWorkflowAction<ViabilityState, ViabilityEvent> {

    @Override
    protected final void doExecute(final StateContext<ViabilityState, ViabilityEvent> context) {
        ViabilityProcess viability = (ViabilityProcess) context.getMessageHeader("process");
        ViabilityResetOutcome outcome = (ViabilityResetOutcome) context.getMessageHeader("reset");
        viability.getViabilityResetOutcomes().add(outcome);
    }
}