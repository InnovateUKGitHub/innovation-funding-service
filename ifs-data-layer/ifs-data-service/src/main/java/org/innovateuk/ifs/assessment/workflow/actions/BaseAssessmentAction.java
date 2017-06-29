package org.innovateuk.ifs.assessment.workflow.actions;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

/**
 * A base class for Assessment-related workflow Actions
 */
public abstract class BaseAssessmentAction extends TestableTransitionWorkflowAction<AssessmentStates, AssessmentOutcomes> {

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Override
    public void doExecute(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Assessment assessment = getAssessmentFromContext(context);
        doExecute(assessment, context);
    }

    private Assessment getAssessmentFromContext(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        return (Assessment) context.getMessageHeader("target");
    }

    protected abstract void doExecute(Assessment assessment, StateContext<AssessmentStates, AssessmentOutcomes> context);
}
