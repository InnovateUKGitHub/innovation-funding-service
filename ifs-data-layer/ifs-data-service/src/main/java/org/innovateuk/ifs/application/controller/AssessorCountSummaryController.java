package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.transactional.AssessorCountSummaryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Controller for exposing statistical data on assessors
 */
@RestController
@RequestMapping("/assessor-count-summary")
public class AssessorCountSummaryController {

    @Autowired
    private AssessorCountSummaryService assessorCountSummaryService;

    private static final String DEFAULT_PAGE_SIZE = "20";

    @GetMapping("/find-by-competition-id/{competitionId}")
    public RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(@PathVariable("competitionId") long competitionId,
                                                                                                 @RequestParam(value = "assessorNameFilter", defaultValue = "") String assessorNameFilter,
                                                                                                 @RequestParam(value = "page",defaultValue = "0") int pageIndex,
                                                                                                 @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return assessorCountSummaryService.getAssessorCountSummariesByCompetitionId(competitionId, trim(assessorNameFilter), pageIndex, pageSize).toGetResponse();
    }
}
