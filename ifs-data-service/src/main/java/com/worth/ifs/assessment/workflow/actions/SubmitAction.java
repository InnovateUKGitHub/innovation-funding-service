package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * The {@code SubmitAction} is used by the assessor. It handles the submit event
 * for an application during assessment.
 * For more info see {@link com.worth.ifs.assessment.workflow.AssessorWorkflowConfig}
 */
public class SubmitAction implements Action<String, String> {

    @Autowired
    AssessmentRepository assessmentRepository;

    @Override
    public void execute(StateContext<String, String> context) {
        Assessment updatedAssessment = (Assessment) context.getMessageHeader("assessment");
        updatedAssessment.setProcessStatus(context.getTransition().getTarget().getId());
        assessmentRepository.save(updatedAssessment);
    }
}
