package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.assessment.dashboard.transactional.ApplicationAssessmentService;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assessment")
public class ApplicationAssessmentController {

    private ApplicationAssessmentService applicationAssessmentService;

    @GetMapping("/assessor/dashboard/competition/{competitionId}")
    public RestResult<List<ApplicationAssessmentResource>> getApplicationsForAssessment(long userId,
                                                                                        @PathVariable("competitionId") long competitionId) {
        return applicationAssessmentService.getApplicationAssessmentResource(userId, competitionId).toGetResponse();
    }
}
