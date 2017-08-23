package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code AssessmentCompleteGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class AssessmentCompleteGuard implements Guard<AssessmentState, AssessmentEvent> {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Override
    public boolean evaluate(StateContext<AssessmentState, AssessmentEvent> context) {
        Assessment assessment = (Assessment) context.getMessageHeader("target");
        return isFeedbackComplete(assessment) && isFundingDecisionComplete(assessment);
    }

    private boolean isFeedbackComplete(Assessment assessment) {
        return assessmentRepository.isFeedbackComplete(assessment.getId());
    }

    private boolean isFundingDecisionComplete(Assessment assessment) {
        return assessment.getFundingDecision() != null;
    }
}
