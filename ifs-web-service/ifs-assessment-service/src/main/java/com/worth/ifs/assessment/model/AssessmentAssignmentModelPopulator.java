package com.worth.ifs.assessment.model;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentAssignmentViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.springframework.beans.factory.annotation.Autowired;

public class AssessmentAssignmentModelPopulator {

    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionService competitionService;


    public AssessmentAssignmentViewModel populateModel(Long assessmentId, Long userId) {
        AssessmentResource assessment = getAssessment(assessmentId);
        ApplicationResource application = getApplication(assessment.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        return new AssessmentAssignmentViewModel();
    }

    private AssessmentResource getAssessment(final Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(final Long applicationId) {
        return applicationService.getById(applicationId);
    }
}
