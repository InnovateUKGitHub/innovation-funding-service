package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackApplicationDetailsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessment Feedback Application Details view.
 */
@Component
public class AssessmentFeedbackApplicationDetailsModelPopulator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private CompetitionService competitionService;

    public AssessmentFeedbackApplicationDetailsViewModel populateModel(Long assessmentId, QuestionResource question) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        ApplicationResource application = applicationService.getById(assessment.getApplication());
        CompetitionResource competition = competitionService.getById(assessment.getCompetition());
        return new AssessmentFeedbackApplicationDetailsViewModel(assessment.getApplication(),
                assessment.getApplicationName(),
                application.getStartDate(),
                application.getDurationInMonths(),
                competition.getAssessmentDaysLeft(),
                competition.getAssessmentDaysLeftPercentage(),
                question.getShortName());
    }

}
