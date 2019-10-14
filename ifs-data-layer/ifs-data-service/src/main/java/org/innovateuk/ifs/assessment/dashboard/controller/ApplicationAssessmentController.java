package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.assessment.dashboard.transactional.AssessorCompetitionDashboardService;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assessment")
public class ApplicationAssessmentController {

    @Autowired
    private ApplicationAssessmentService applicationAssessmentService;

    @Autowired
    private AssessorCompetitionDashboardService assessorCompetitionDashboardService;

    @GetMapping("/user/{userId}/competition/{competitionId}/dashboard")
    public RestResult<AssessorCompetitionDashboardResource> findByUserAndCompetition(
            @PathVariable("userId") long userId,
            @PathVariable("competitionId") long competitionId) {

        List<ApplicationAssessmentResource> applicationAssessmentResource = applicationAssessmentService.getApplicationAssessmentResource(userId, competitionId).getSuccess();
        return assessorCompetitionDashboardService.getAssessorCompetitionDashboardResource(competitionId, applicationAssessmentResource).toGetResponse();
    }
}
