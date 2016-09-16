package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

/**
 * {@code AssessmentGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Configuration
public class AssessmentGuard implements Guard<AssessmentStates, AssessmentOutcomes> {

    @Override
    public boolean evaluate(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Object assessmentObject = context.getMessageHeader("assessment");
        Object processRoleId = context.getMessageHeader("processRoleId");

        if(isAssessment(assessmentObject) &&
                isTypeOfLong(processRoleId)) {
            return true;
        }
        return false;
    }

    private boolean isTypeOfLong(Object object) {
        return object!=null && object instanceof Long;
    }

    private boolean isAssessment(Object object) {
        return object!=null && object instanceof Assessment;
    }
}
