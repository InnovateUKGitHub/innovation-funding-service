package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.RecommendedValue;
import com.worth.ifs.assessment.workflow.configuration.AssessorWorkflowConfig;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;

import java.util.Optional;

/**
 * The {@code RecommendAction} is used by the assessor. It handles the recommendation
 * assessment event applied to an application.
 * For more info see {@link AssessorWorkflowConfig}
 */
public class RecommendAction extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, ActivityState newState, Optional<ProcessOutcome> updatedProcessOutcome) {

        Optional<ProcessOutcome> processOutcome = assessment.getProcessOutcomes()
                .stream()
                .filter(p -> AssessmentOutcomes.RECOMMEND.getType().equals(p.getOutcomeType()))
                .findFirst();

        ProcessOutcome assessmentOutcome = processOutcome.orElse(new ProcessOutcome());

        assessmentOutcome.setOutcome(updatedProcessOutcome.get().getOutcome());
        assessmentOutcome.setDescription(updatedProcessOutcome.get().getDescription());
        assessmentOutcome.setComment(updatedProcessOutcome.get().getComment());
        assessmentOutcome.setOutcomeType(AssessmentOutcomes.RECOMMEND.getType());

        if(!RecommendedValue.EMPTY.toString().equals(assessmentOutcome.getOutcome())) {
            assessment.setActivityState(newState);
        }

        if (assessmentOutcome.getId() == null) {
            assessmentOutcome.setProcess(assessment);
            assessment.getProcessOutcomes().add(assessmentOutcome);
        }
        // If we do not save the entity first then hibernate creates two entries for it when saving the assessment
        processOutcomeRepository.save(assessmentOutcome);
        assessmentRepository.save(assessment);
    }
}
