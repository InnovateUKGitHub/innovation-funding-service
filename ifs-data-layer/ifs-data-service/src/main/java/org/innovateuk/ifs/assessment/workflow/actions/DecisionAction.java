package org.innovateuk.ifs.assessment.workflow.actions;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentDecisionOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * The {@code DecisionAction} is used when an assessment has been accepted and a funding decision is added by the assessor.
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class DecisionAction extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, StateContext<AssessmentState, AssessmentEvent> context) {
        AssessmentDecisionOutcome assessmentDecisionOutcome =
                (AssessmentDecisionOutcome) context.getMessageHeader("decision");

        // Update the existing outcome if it exists
        AssessmentDecisionOutcome existingOutcome = assessment.getDecision();
        if (existingOutcome != null) {
            copyOutcome(assessmentDecisionOutcome, existingOutcome);
        } else {
            assessment.setDecision(assessmentDecisionOutcome);
        }
    }

    private void copyOutcome(AssessmentDecisionOutcome source, AssessmentDecisionOutcome target) {
        target.setFundingConfirmation(source.isFundingConfirmation());
        target.setFeedback(source.getFeedback());
        target.setComment(source.getComment());
    }
}
