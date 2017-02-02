package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code AssessmentRejectOutcomeGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class AssessmentRejectOutcomeGuard implements Guard<AssessmentStates, AssessmentOutcomes> {

    @Override
    public boolean evaluate(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Object rejectionObject = context.getMessageHeader("rejection");
        return isAssessmentRejectOutcome(rejectionObject);
    }

    private boolean isAssessmentRejectOutcome(Object object) {
        return object != null && object instanceof AssessmentRejectOutcome;
    }
}
