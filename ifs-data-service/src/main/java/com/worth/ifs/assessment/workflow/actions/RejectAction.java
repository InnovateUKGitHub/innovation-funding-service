package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class RejectAction implements Action<String, String> {
    @Autowired
    AssessmentRepository assessmentRepository;

    public RejectAction() {

    }

    @Override
    public void execute(StateContext<String, String> context) {
        Assessment updatedAssessment = (Assessment) context.getMessageHeader("assessment");
        Long applicationId = (Long) context.getMessageHeader("applicationId");
        Long assessorId = (Long) context.getMessageHeader("assessorId");

        Assessment assessment = assessmentRepository.findOneByAssessorAndApplication(assessorId, applicationId);
        if (assessment != null) {
            assessment.setProcessStatus(context.getTransition().getTarget().getId());
            assessment.setDecisionReason(updatedAssessment.getDecisionReason());
            assessment.setObservations(updatedAssessment.getObservations());
            assessmentRepository.save(assessment);
        }
    }
}
