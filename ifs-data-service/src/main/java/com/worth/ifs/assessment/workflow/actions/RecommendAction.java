package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentOutcomes;
import com.worth.ifs.assessment.domain.RecommendedValue;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.Optional;

/**
 * The {@code RecommendAction} is used by the assessor. It handles the recommendation
 * assessment event applied to an application.
 * For more info see {@link com.worth.ifs.assessment.workflow.AssessorWorkflowConfig}
 */
public class RecommendAction implements Action<String, String> {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    AssessmentRepository assessmentRepository;

    @Override
    public void execute(StateContext<String, String> context) {
        ProcessOutcome updatedProcessOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        Long processRoleId = (Long) context.getMessageHeader("processRoleId");
        Assessment assessment = assessmentRepository.findOneByProcessRoleId(processRoleId);

        if(assessment!=null) {
            Optional<ProcessOutcome> processOutcome = assessment.getProcessOutcomes()
                    .stream()
                    .filter(p -> AssessmentOutcomes.RECOMMEND.getType().equals(p.getOutcomeType()))
                    .findFirst();

            ProcessOutcome assessmentOutcome = processOutcome.orElse(new ProcessOutcome());

            assessmentOutcome.setOutcome(updatedProcessOutcome.getOutcome());
            assessmentOutcome.setDescription(updatedProcessOutcome.getDescription());
            assessmentOutcome.setComment(updatedProcessOutcome.getComment());
            assessmentOutcome.setOutcomeType(AssessmentOutcomes.RECOMMEND.getType());

            if(!RecommendedValue.EMPTY.toString().equals(assessmentOutcome)) {
                assessment.setProcessStatus(context.getTransition().getTarget().getId());
            }


            if (assessmentOutcome.getId() == null) {
                assessmentOutcome.setProcess(assessment);
                assessment.getProcessOutcomes().add(assessmentOutcome);
            }
            assessmentRepository.save(assessment);
        }
    }
}
