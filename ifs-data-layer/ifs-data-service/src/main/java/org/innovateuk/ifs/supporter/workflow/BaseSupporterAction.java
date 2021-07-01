package org.innovateuk.ifs.supporter.workflow;

import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.resource.SupporterEvent;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.statemachine.StateContext;

public abstract class BaseSupporterAction extends TestableTransitionWorkflowAction<SupporterState, SupporterEvent> {

    @Override
    public void doExecute(StateContext<SupporterState, SupporterEvent> context) {
        SupporterAssignment assessment = getAssessmentFromContext(context);
        doExecute(assessment, context);
    }

    private SupporterAssignment getAssessmentFromContext(StateContext<SupporterState, SupporterEvent> context) {
        return (SupporterAssignment) context.getMessageHeader("target");
    }

    protected abstract void doExecute(SupporterAssignment assessment, StateContext<SupporterState, SupporterEvent> context);
}
