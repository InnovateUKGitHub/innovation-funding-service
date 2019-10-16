package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.assessment.dashboard.transactional.AssessmentCompetitionDashboardService;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessment")
public class AssessmentCompetitionDashboardController {

    @Autowired
    private AssessmentCompetitionDashboardService assessorCompetitionDashboardService;

    @GetMapping("/user/{userId}/competition/{competitionId}/dashboard")
    public RestResult<AssessorCompetitionDashboardResource> findByUserAndCompetition(
            @PathVariable("userId") long userId,
            @PathVariable("competitionId") long competitionId) {

        return assessorCompetitionDashboardService.getAssessorCompetitionDashboardResource(userId, competitionId).toGetResponse();
    }
}