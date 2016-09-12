package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * The AcceptAction is used by the assessor. It handles the accepting event
 * for an application during assessment.
 * For more info see {@link com.worth.ifs.assessment.workflow.AssessorWorkflowConfig}
 */
@Component
public class AcceptAction implements Action<String, String> {

    @Autowired
    AssessmentRepository assessmentRepository;

    @Override
    public void execute(StateContext<String, String> context) {
        Long processRoleId = (Long) context.getMessageHeader("processRoleId");
        Assessment assessment = assessmentRepository.findOneByParticipantId(processRoleId);

        if(assessment !=null) {
            assessment.setProcessStatus(context.getTransition().getTarget().getId());
            assessmentRepository.save(assessment);
        }
    }
}
