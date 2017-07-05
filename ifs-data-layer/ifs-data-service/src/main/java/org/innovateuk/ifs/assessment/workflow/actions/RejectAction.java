package org.innovateuk.ifs.assessment.workflow.actions;

import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
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
    protected void doExecute(Assessment assessment, StateContext<AssessmentStates, AssessmentOutcomes> context) {
        AssessmentRejectOutcome assessmentRejectOutcome = (AssessmentRejectOutcome) context.getMessageHeader("rejection");
        assessment.setRejection(assessmentRejectOutcome);
    }
}
