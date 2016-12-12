package org.innovateuk.ifs.assessment.workflow.actions;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.assessment.resource.AssessmentOutcomes.REJECT;

/**
 * The {@code RejectAction} is used by the assessor. It handles the rejection event
 * for an application during assessment.
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class RejectAction extends BaseAssessmentAction {

    @Override
    protected void doExecute(Assessment assessment, Optional<ProcessOutcome> processOutcome) {
        ProcessOutcome processOutcomeValue = processOutcome.get();

        processOutcomeValue.setProcess(assessment);
        processOutcomeValue.setOutcomeType(REJECT.getType());
        assessment.getProcessOutcomes().add(processOutcomeValue);
    }
}
