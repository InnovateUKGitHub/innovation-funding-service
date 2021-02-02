package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.transactional.AssessmentPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AssessmentPeriodController exposes AssessmentPeriod data and operations through a REST API
 */
@RestController
@RequestMapping("/assessment-period")
public class AssessmentPeriodController {

    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    @GetMapping("/{competitionId}/get-by-index")
    public RestResult<AssessmentPeriodResource> getAssessmentPeriodByCompetitionIdAndIndex(
            @RequestParam("index") final Integer index,
            @PathVariable("competitionId") final Long competitionId) {
        return assessmentPeriodService.getAssessmentPeriodByCompetitionIdAndIndex(competitionId, index).toGetResponse();
    }

    @PostMapping("/{competitionId}")
    public RestResult<AssessmentPeriodResource> create(@RequestParam("index") final Integer index,
                                                @PathVariable("competitionId") final Long competitionId) {
        return assessmentPeriodService.create(competitionId, index).toPostCreateResponse();
    }

}
