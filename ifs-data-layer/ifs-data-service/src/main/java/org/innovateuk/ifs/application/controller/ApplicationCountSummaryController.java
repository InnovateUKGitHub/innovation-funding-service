package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.application.transactional.ApplicationCountSummaryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for exposing statistical data on applications
 */
@RestController
@RequestMapping("/application-count-summary")
public class ApplicationCountSummaryController {

    @Autowired
    private ApplicationCountSummaryService applicationCountSummaryService;

    private static final String DEFAULT_PAGE_SIZE = "20";

    @GetMapping("/find-by-competition-id/{competitionId}")
    public RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(@PathVariable long competitionId,
                                                                                                       @RequestParam(value = "page",defaultValue = "0") int pageIndex,
                                                                                                       @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                                                                       @RequestParam(value = "filter", required = false) Optional<String> filter) {
        return applicationCountSummaryService.getApplicationCountSummariesByCompetitionId(competitionId, pageIndex, pageSize, filter).toGetResponse();
    }

    @GetMapping("/find-by-competition-id-and-assessor-id/{competitionId}/{assessorId}")
    public RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndAssessorId(@PathVariable long competitionId,
                                                                                                                    @PathVariable long assessorId,
                                                                                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                                                    @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
                                                                                                                    @RequestParam(value = "sort") Sort sort,
                                                                                                                    @RequestParam(value = "filter") String filter) {
        return applicationCountSummaryService.getApplicationCountSummariesByCompetitionIdAndAssessorId(competitionId, assessorId, page, size, sort, filter).toGetResponse();
    }
    @GetMapping("/find-ids-by-competition-id-and-assessor-id/{competitionId}/{assessorId}")
    public RestResult<List<Long>> getApplicationIdsByCompetitionIdAndAssessorId(@PathVariable long competitionId,
                                                                                @PathVariable long assessorId,
                                                                                @RequestParam(value = "filter") String filter) {
        return applicationCountSummaryService.getApplicationIdsByCompetitionIdAndAssessorId(competitionId, assessorId, filter).toGetResponse();
    }
}
