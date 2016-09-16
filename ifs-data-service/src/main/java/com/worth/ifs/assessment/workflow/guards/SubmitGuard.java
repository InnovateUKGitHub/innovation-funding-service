package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

import static com.worth.ifs.assessment.resource.AssessmentStates.SUBMITTED;

/**
 * {@code SubmitGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
public class SubmitGuard implements Guard<AssessmentStates, AssessmentOutcomes> {

    @Override
    public boolean evaluate(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Assessment assessment = (Assessment) context.getMessageHeader("assessment");
        if(assessment ==null)
            return false;

        return assessment.isStarted() && !assessment.isInState(SUBMITTED);
    }
}
