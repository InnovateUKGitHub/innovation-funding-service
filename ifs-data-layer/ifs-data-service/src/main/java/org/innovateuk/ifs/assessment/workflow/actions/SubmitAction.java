package org.innovateuk.ifs.assessment.workflow.actions;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;

/**
 * The {@code RejectAction} is used by the assessor. It handles the rejection event
 * for an application during assessment.
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class SubmitAction extends BaseAssessmentAction {

    @Autowired
    private AssessorFormInputResponseRepository assessorFormInputResponseRepository;

    @Autowired
    private AverageAssessorScoreRepository averageAssessorScoreRepository;

    @Override
    protected void doExecute(Assessment assessment, StateContext<AssessmentState, AssessmentEvent> context) {
        List<AssessorFormInputResponse> responses = assessorFormInputResponseRepository.findByAssessmentTargetId(assessment.getTarget().getId());
        BigDecimal percentage = getAveragePercentage(responses);

        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(assessment.getTarget().getId());

        if (averageAssessorScore.isPresent()) {
            averageAssessorScore.get().setScore(percentage);
        } else {
            averageAssessorScoreRepository.save(new AverageAssessorScore(assessment.getTarget(), percentage));
        }
    }

    private BigDecimal getAveragePercentage(List<AssessorFormInputResponse> responses) {
        return BigDecimal.valueOf(responses.stream()
                .filter(input -> input.getFormInput().getType() == ASSESSOR_SCORE)
                .filter(response -> response.getValue() != null)
                .mapToDouble(value -> (Double.parseDouble(value.getValue()) / value.getFormInput().getQuestion().getAssessorMaximumScore()) * 100.0)
                .average()
                .orElse(0.0)).setScale(1, BigDecimal.ROUND_HALF_UP);
    }
}
