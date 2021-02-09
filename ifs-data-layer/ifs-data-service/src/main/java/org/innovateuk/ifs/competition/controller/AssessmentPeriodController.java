package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.transactional.AssessmentPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AssessmentPeriodController exposes AssessmentPeriod data and operations through a REST API
 */
@RestController
@RequestMapping("/assessment-period")
public class AssessmentPeriodController {

    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    @GetMapping("/{competitionId}")
    public RestResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionIdAndIndex(
            @PathVariable("competitionId") final long competitionId) {
        return assessmentPeriodService.getAssessmentPeriodByCompetitionId(competitionId).toGetResponse();
    }

    @PostMapping("/{competitionId}")
    public RestResult<AssessmentPeriodResource> create(@RequestParam final int index,
                                                       @PathVariable("competitionId") final long competitionId) {
        return assessmentPeriodService.create(competitionId, index).toPostCreateResponse();
    }
}
