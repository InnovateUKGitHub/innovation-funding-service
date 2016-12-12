package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code ProcessOutcomeGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class ProcessOutcomeGuard implements Guard<AssessmentStates, AssessmentOutcomes> {

    @Override
    public boolean evaluate(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Object processOutcomeObject = context.getMessageHeader("processOutcome");
        return isProcessOutcome(processOutcomeObject);
    }

    private boolean isProcessOutcome(Object object) {
        return object!=null && object instanceof ProcessOutcome;
    }
}
