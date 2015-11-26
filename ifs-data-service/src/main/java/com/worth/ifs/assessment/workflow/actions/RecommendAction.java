package com.worth.ifs.assessment.workflow.actions;

import com.worth.ifs.assessment.domain.Recommendation;
import com.worth.ifs.assessment.domain.RecommendedValue;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

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
        Recommendation updatedRecommendation = (Recommendation) context.getMessageHeader("recommendation");
        Long applicationId = (Long) context.getMessageHeader("applicationId");
        Long assessorId = (Long) context.getMessageHeader("assessorId");
        Recommendation recommendation = assessmentRepository.findOneByAssessorIdAndApplicationId(assessorId, applicationId);

        if(recommendation !=null) {
            recommendation.setSummary(updatedRecommendation.getRecommendedValue(),
                    updatedRecommendation.getSuitableFeedback(),
                    updatedRecommendation.getComments(),
                    updatedRecommendation.getOverallScore());

            if(!recommendation.getRecommendedValue().equals(RecommendedValue.EMPTY)) {
                recommendation.setProcessStatus(context.getTransition().getTarget().getId());
            }
            assessmentRepository.save(recommendation);
        }
    }
}
