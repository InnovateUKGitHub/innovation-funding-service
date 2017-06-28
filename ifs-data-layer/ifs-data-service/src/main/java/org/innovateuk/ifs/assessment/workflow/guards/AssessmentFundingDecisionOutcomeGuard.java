package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code AssessmentFundingDecisionOutcomeGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class AssessmentFundingDecisionOutcomeGuard implements Guard<AssessmentStates, AssessmentOutcomes> {

    @Override
    public boolean evaluate(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Object fundingDecisionObject = context.getMessageHeader("fundingDecision");
        return isAssessmentFundingDecisionOutcome(fundingDecisionObject);
    }

    private boolean isAssessmentFundingDecisionOutcome(Object object) {
        return object != null && object instanceof AssessmentFundingDecisionOutcome;
    }
}
