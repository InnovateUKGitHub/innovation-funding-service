package org.innovateuk.ifs.assessment.workflow.actions;

import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * The {@code RejectAction} is used by the assessor. It handles the rejection event
 * for an application during assessment.
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class RejectAction extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, StateContext<AssessmentState, AssessmentEvent> context) {
        AssessmentRejectOutcome assessmentRejectOutcome = (AssessmentRejectOutcome) context.getMessageHeader("rejection");
        assessment.setRejection(assessmentRejectOutcome);
    }
}
