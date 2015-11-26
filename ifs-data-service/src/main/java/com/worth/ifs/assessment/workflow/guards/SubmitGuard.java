package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.domain.Recommendation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

/**
 * {@code SubmitGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
public class SubmitGuard implements Guard<String, String> {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public boolean evaluate(StateContext<String, String> context) {
        Recommendation recommendation = (Recommendation) context.getMessageHeader("recommendation");
        if(recommendation ==null)
            return false;

        return (recommendation.hasAssessmentStarted() && !recommendation.isSubmitted());
    }
}
