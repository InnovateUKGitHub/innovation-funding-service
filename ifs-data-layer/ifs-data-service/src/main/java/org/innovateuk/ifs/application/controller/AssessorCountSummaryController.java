package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.transactional.AssessorCountSummaryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for exposing statistical data on assessors
 */
@RestController
@RequestMapping("/assessorCountSummary")
public class AssessorCountSummaryController {

    @Autowired
    private AssessorCountSummaryService assessorCountSummaryService;

    private static final String DEFAULT_PAGE_SIZE = "20";

    @GetMapping("/findByCompetitionId/{competitionId}")
    public RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(@PathVariable("competitionId") long competitionId,
                                                                                                 @RequestParam(value = "page",defaultValue = "0") int pageIndex,
                                                                                                 @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                                                                 @RequestParam(value = "filter", required = false) Optional<String> filter) {
        return assessorCountSummaryService.getAssessorCountSummariesByCompetitionId(competitionId, pageIndex, pageSize, filter).toGetResponse();
    }
}
