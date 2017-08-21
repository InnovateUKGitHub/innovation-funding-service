package org.innovateuk.ifs.assessment.workflow.actions;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

/**
 * A base class for Assessment-related workflow Actions
 */
public abstract class BaseAssessmentAction extends TestableTransitionWorkflowAction<AssessmentState, AssessmentEvent> {

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Override
    public void doExecute(StateContext<AssessmentState, AssessmentEvent> context) {
        Assessment assessment = getAssessmentFromContext(context);
        doExecute(assessment, context);
    }

    private Assessment getAssessmentFromContext(StateContext<AssessmentState, AssessmentEvent> context) {
        return (Assessment) context.getMessageHeader("target");
    }

    protected abstract void doExecute(Assessment assessment, StateContext<AssessmentState, AssessmentEvent> context);
}
