package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.workflow.configuration.AssessorWorkflow;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The AcceptAction is used by the assessor. It handles the accepting event
 * for an application during assessment.
 * For more info see {@link AssessorWorkflow}
 */
@Component
public class AcceptAction  extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, ActivityState newState, Optional<ProcessOutcome> updatedProcessOutcome) {
        assessment.setActivityState(newState);
        assessmentRepository.save(assessment);
    }
}
