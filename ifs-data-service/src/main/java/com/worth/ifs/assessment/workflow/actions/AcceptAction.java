package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The AcceptAction is used by the assessor. It handles the accepting event
 * for an application during assessment.
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class AcceptAction  extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, ActivityState newState, Optional<ProcessOutcome> updatedProcessOutcome) {
        ProcessOutcome processOutcome = updatedProcessOutcome.get();

        processOutcome.setProcess(assessment);
        assessment.getProcessOutcomes().add(processOutcome);
        assessment.setActivityState(newState);
        processOutcome.setOutcomeType(AssessmentOutcomes.ACCEPT.getType());
        // If we do not save the entity first then hibernate creates two entries for it when saving the assessment
        processOutcomeRepository.save(processOutcome);
        assessmentRepository.save(assessment);
    }
}
