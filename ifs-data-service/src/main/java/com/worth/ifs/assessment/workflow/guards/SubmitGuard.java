package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.domain.Assessment;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

import static com.worth.ifs.assessment.resource.AssessmentStates.SUBMITTED;

/**
 * {@code SubmitGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
public class SubmitGuard implements Guard<String, String> {

    @Override
    public boolean evaluate(StateContext<String, String> context) {
        Assessment assessment = (Assessment) context.getMessageHeader("assessment");
        if(assessment ==null)
            return false;

        return assessment.isStarted() && !assessment.isInState(SUBMITTED);
    }
}
