package org.innovateuk.ifs.assessment.workflow.actions;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * The {@code FundingDecisionAction} is used when an assessment has been accepted and a funding decision is added by the assessor.
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class FundingDecisionAction extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, StateContext<AssessmentStates, AssessmentOutcomes> context) {
        AssessmentFundingDecisionOutcome assessmentFundingDecisionOutcome =
                (AssessmentFundingDecisionOutcome) context.getMessageHeader("fundingDecision");

        // Update the existing outcome if it exists
        AssessmentFundingDecisionOutcome existingOutcome = assessment.getFundingDecision();
        if (existingOutcome != null) {
            copyOutcome(assessmentFundingDecisionOutcome, existingOutcome);
        } else {
            assessment.setFundingDecision(assessmentFundingDecisionOutcome);
        }
    }

    private void copyOutcome(AssessmentFundingDecisionOutcome source, AssessmentFundingDecisionOutcome target) {
        target.setFundingConfirmation(source.isFundingConfirmation());
        target.setFeedback(source.getFeedback());
        target.setComment(source.getComment());
    }
}
