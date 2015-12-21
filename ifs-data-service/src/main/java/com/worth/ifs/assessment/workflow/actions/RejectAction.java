package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * The {@code RejectAction} is used by the assessor. It handles the rejection event
 * for an application during assessment.
 * For more info see {@link com.worth.ifs.assessment.workflow.AssessorWorkflowConfig}
 */
@Component
public class RejectAction implements Action<String, String> {
    @Autowired
    AssessmentRepository assessmentRepository;

    public RejectAction() {

    }

    @Override
    public void execute(StateContext<String, String> context) {
        ProcessOutcome processOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        Long processRoleId = (Long) context.getMessageHeader("processRoleId");

        Assessment assessment = assessmentRepository.findOneByProcessRoleId(processRoleId);
        if (assessment != null) {
            assessment.setProcessStatus(context.getTransition().getTarget().getId());
            assessment.getProcessOutcomes().add(processOutcome);
            assessmentRepository.save(assessment);
        }
    }
}
