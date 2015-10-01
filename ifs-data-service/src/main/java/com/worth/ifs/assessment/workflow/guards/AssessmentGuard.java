package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.domain.AssessmentEvents;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

@Configuration
public class AssessmentGuard implements Guard<String, String> {

    @Override
    public boolean evaluate(StateContext<String, String> context) {
        Object assessmentObject = context.getMessageHeader("assessment");
        Object applicationId = context.getMessageHeader("applicationId");
        Object assessorId = context.getMessageHeader("assessorId");

        if(assessmentObject!=null && assessmentObject instanceof Assessment &&
                applicationId != null && applicationId instanceof Long &&
                assessorId != null && assessorId instanceof Long) {
            return true;
        }
        return false;
    }

}
