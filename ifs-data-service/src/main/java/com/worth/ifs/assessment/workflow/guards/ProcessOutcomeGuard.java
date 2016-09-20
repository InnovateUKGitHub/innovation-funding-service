package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code AssessmentGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class ProcessOutcomeGuard implements Guard<AssessmentStates, AssessmentOutcomes> {

    @Override
    public boolean evaluate(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Object processOutcomeObject = context.getMessageHeader("processOutcome");
        Object processRoleId = context.getMessageHeader("processRoleId");

        if(isProcessOutcome(processOutcomeObject) &&
                isTypeOfLong(processRoleId)) {
            return true;
        }
        return false;
    }

    private boolean isTypeOfLong(Object object) {
        return object!=null && object instanceof Long;
    }

    private boolean isProcessOutcome(Object object) {
        return object!=null && object instanceof ProcessOutcome;
    }
}
