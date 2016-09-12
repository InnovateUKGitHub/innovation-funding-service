package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
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


    @Autowired
    ProcessOutcomeRepository processOutcomeRepository;

    public RejectAction() {
    	// no-arg constructor
    }

    @Override
    public void execute(StateContext<String, String> context) {
        ProcessOutcome processOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        Long processRoleId = (Long) context.getMessageHeader("processRoleId");

        Assessment assessment = assessmentRepository.findOneByParticipantId(processRoleId);
        if (assessment != null) {

            processOutcome.setProcess(assessment);
            assessment.getProcessOutcomes().add(processOutcome);
            assessment.setProcessStatus(context.getTransition().getTarget().getId());
            processOutcome.setOutcomeType(AssessmentOutcomes.REJECT.getType());
            // If we do not save the entity first then hibernate creates two entries for it when saving the assessment
            processOutcomeRepository.save(processOutcome);
            assessmentRepository.save(assessment);
        }
    }
}
