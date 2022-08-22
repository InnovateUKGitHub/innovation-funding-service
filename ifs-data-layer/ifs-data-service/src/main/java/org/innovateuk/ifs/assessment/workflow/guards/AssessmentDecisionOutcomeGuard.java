package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.assessment.domain.AssessmentDecisionOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code AssessmentDecisionOutcomeGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class AssessmentDecisionOutcomeGuard implements Guard<AssessmentState, AssessmentEvent> {

    @Override
    public boolean evaluate(StateContext<AssessmentState, AssessmentEvent> context) {
        Object decisionObject = context.getMessageHeader("decision");
        return isAssessmentDecisionOutcome(decisionObject);
    }

    private boolean isAssessmentDecisionOutcome(Object object) {
        return object != null && object instanceof AssessmentDecisionOutcome;
    }
}
