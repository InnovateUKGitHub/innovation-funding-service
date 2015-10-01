package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.domain.Assessment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

public class SubmitGuard implements Guard<String, String> {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public boolean evaluate(StateContext<String, String> context) {
        Assessment assessment = (Assessment) context.getMessageHeader("assessment");
        if(assessment==null)
            return false;

        return (assessment.hasAssessmentStarted() && !assessment.isSubmitted());
    }
}
