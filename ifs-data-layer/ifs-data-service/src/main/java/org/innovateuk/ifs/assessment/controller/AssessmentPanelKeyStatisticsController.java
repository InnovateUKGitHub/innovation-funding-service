package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssessmentPanelKeyStatisticsController {

    @Autowired
    private AssessmentService assessmentService;

    @GetMapping("/panel/{competitionId}")
    public RestResult<AssessmentPanelKeyStatisticsResource> getAssessmentPanelKeyStatistics(@PathVariable("competitionId") long competitionId) {
        return assessmentService.getAssessmentPanelKeyStatistics(competitionId).toGetResponse();
    }
}
