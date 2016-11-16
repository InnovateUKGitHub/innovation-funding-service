package com.worth.ifs.assessment.model;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackApplicationDetailsViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
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
        return new AssessmentFeedbackApplicationDetailsViewModel(competition.getAssessmentDaysLeft(), competition.getAssessmentDaysLeftPercentage(), competition, application, question.getShortName());
    }

}
