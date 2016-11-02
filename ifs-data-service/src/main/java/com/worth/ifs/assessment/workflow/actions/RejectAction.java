package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.worth.ifs.assessment.resource.AssessmentOutcomes.REJECT;

/**
 * The {@code RejectAction} is used by the assessor. It handles the rejection event
 * for an application during assessment.
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class RejectAction extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, ActivityState newState, Optional<ProcessOutcome> updatedProcessOutcome) {
        super.doExecute(assessment, newState, updatedProcessOutcome);

        ProcessOutcome processOutcome = updatedProcessOutcome.get();

        processOutcome.setProcess(assessment);
        processOutcome.setOutcomeType(REJECT.getType());
        assessment.getProcessOutcomes().add(processOutcome);
    }
}
