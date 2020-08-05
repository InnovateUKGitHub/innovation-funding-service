package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackApplicationDetailsViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessment Feedback Application Details view.
 */
@Component
public class AssessmentFeedbackApplicationDetailsModelPopulator extends AssessmentModelPopulator<AssessmentFeedbackApplicationDetailsViewModel> {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    public AssessmentFeedbackApplicationDetailsViewModel populate(long assessmentId, QuestionResource question) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        CompetitionResource competition = competitionRestService.getCompetitionById(assessment.getCompetition()).getSuccess();

        ApplicationResource application = applicationService.getById(assessment.getApplication());

        return new AssessmentFeedbackApplicationDetailsViewModel(
                assessment.getApplication(),
                assessment.getApplicationName(),
                application.getStartDate(),
                application.getDurationInMonths(),
                competition.getAssessmentDaysLeft(),
                competition.getAssessmentDaysLeftPercentage(),
                question.getShortName());
    }
}
