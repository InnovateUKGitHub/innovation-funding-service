package org.innovateuk.ifs.assessment.interview.workflow.actions;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanelMessageOutcome;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelEvent;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class NotifyAssessmentInterviewPanelAction extends BaseAssessmentInterviewPanelAction {

    @Override
    protected void doExecute(AssessmentInterviewPanel assessmentInterviewPanel, StateContext<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> context) {
        AssessmentInterviewPanelMessageOutcome assessmentInterviewPanelMessageOutcome =
                (AssessmentInterviewPanelMessageOutcome) context.getMessageHeader("message");
        assessmentInterviewPanel.setMessage(assessmentInterviewPanelMessageOutcome);
    }
}