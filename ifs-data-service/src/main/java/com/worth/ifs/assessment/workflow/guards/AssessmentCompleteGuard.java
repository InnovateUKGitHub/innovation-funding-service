package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code AssessmentCompleteGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class AssessmentCompleteGuard implements Guard<AssessmentStates, AssessmentOutcomes> {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Override
    public boolean evaluate(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Assessment assessment = (Assessment) context.getMessageHeader("assessment");
        return isFeedbackComplete(assessment) && isFundingDecisionComplete(assessment);
    }

    private boolean isFeedbackComplete(Assessment assessment) {
        return assessmentRepository.isFeedbackComplete(assessment.getId());
    }

    private boolean isFundingDecisionComplete(Assessment assessment) {
        return assessment.getLastOutcome(AssessmentOutcomes.FUNDING_DECISION).isPresent();
    }
}