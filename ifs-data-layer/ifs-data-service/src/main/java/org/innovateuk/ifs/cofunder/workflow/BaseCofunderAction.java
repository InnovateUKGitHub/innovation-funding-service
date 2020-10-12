package org.innovateuk.ifs.cofunder.workflow;

import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.resource.CofunderEvent;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.statemachine.StateContext;

public abstract class BaseCofunderAction extends TestableTransitionWorkflowAction<CofunderState, CofunderEvent> {

    @Override
    public void doExecute(StateContext<CofunderState, CofunderEvent> context) {
        CofunderAssignment assessment = getAssessmentFromContext(context);
        doExecute(assessment, context);
    }

    private CofunderAssignment getAssessmentFromContext(StateContext<CofunderState, CofunderEvent> context) {
        return (CofunderAssignment) context.getMessageHeader("target");
    }

    protected abstract void doExecute(CofunderAssignment assessment, StateContext<CofunderState, CofunderEvent> context);
}
