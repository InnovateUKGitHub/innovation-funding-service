package com.worth.ifs.project.workflow.projectdetails.guards;

import com.worth.ifs.assessment.domain.Assessment;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

/**
 * This asserts that all mandatory Project Details have been included prior to allowing them to be submitted.
 */
@Configuration
public class ProjectDetailsSuppliedGuard implements Guard<String, String> {

    @Override
    public boolean evaluate(StateContext<String, String> context) {
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
