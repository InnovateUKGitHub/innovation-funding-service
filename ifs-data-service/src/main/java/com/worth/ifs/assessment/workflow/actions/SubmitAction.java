package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;

import java.util.Optional;

/**
 * The {@code SubmitAction} is used by the assessor. It handles the submit event
 * for an application during assessment.
 * For more info see {@link com.worth.ifs.assessment.workflow.AssessorWorkflowConfig}
 */
public class SubmitAction extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, ActivityState newState, Optional<ProcessOutcome> updatedProcessOutcome) {
        assessment.setActivityState(newState);
        assessmentRepository.save(assessment);
    }
}
