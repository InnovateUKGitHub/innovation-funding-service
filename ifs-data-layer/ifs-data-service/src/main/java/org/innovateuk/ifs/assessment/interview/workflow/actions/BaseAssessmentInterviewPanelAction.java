package org.innovateuk.ifs.assessment.interview.workflow.actions;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewPanelRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelEvent;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

/**
 * A base class for Assessment Interview Panel-related workflow Actions
 */
public abstract class BaseAssessmentInterviewPanelAction extends TestableTransitionWorkflowAction<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> {

    @Autowired
    protected AssessmentInterviewPanelRepository assessmentInterviewPanelRepository;

    @Override
    public void doExecute(StateContext<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> context) {
        AssessmentInterviewPanel assessmentInterviewPanel = getAssessmentInterviewPanelFromContext(context);
        doExecute(assessmentInterviewPanel, context);
    }

    private AssessmentInterviewPanel getAssessmentInterviewPanelFromContext(StateContext<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> context) {
        return (AssessmentInterviewPanel) context.getMessageHeader("target");
    }

    protected abstract void doExecute(AssessmentInterviewPanel assessment, StateContext<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> context);
}
