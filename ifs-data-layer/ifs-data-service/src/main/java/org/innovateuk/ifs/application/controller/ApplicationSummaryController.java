package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.transactional.ApplicationSummaryService;
import org.innovateuk.ifs.application.transactional.CompetitionSummaryService;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * ApplicationSummaryController exposes application summary data and operations through a REST API.
 * It is mainly used at present for getting summaries of applications for showing in the competition manager views.
 */
@RestController
@ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
@RequestMapping({"/applicationSummary", "/application-summary"})
public class ApplicationSummaryController {
    private static final String DEFAULT_PAGE_SIZE = "20";

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private CompetitionSummaryService competitionSummaryService;

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetition/{competitionId}", "/find-by-competition/{competitionId}"})
    public RestResult<ApplicationSummaryPageResource> getApplicationSummaryByCompetitionId(
            @PathVariable("competitionId") long competitionId,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int pageIndex,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "filter", required = false) Optional<String> filter) {
        return applicationSummaryService.getApplicationSummariesByCompetitionId(competitionId, sortBy, pageIndex, pageSize, filter).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getCompetitionSummary/{id}", "/get-competition-summary/{id}"})
    public RestResult<CompetitionSummaryResource> getCompetitionSummary(@PathVariable("id") Long id) {
        return competitionSummaryService.getCompetitionSummaryByCompetitionId(id).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetition/{competitionId}/all-submitted", "/find-by-competition/{competitionId}/all-submitted"})
    public RestResult<List<Long>> getAllSubmittedApplicationIdsByCompetitionId(
            @PathVariable("competitionId") long competitionId,
            @RequestParam(value = "filter", required = false) Optional<String> filter,
            @RequestParam(value = "fundingFilter", required = false) Optional<FundingDecisionStatus> fundingFilter) {
        return applicationSummaryService.getAllSubmittedApplicationIdsByCompetitionId(competitionId, filter, fundingFilter).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetition/{competitionId}/submitted", "/find-by-competition/{competitionId}/submitted"})
    public RestResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(
            @PathVariable("competitionId") long competitionId,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int pageIndex,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "filter", required = false) Optional<String> filter,
            @RequestParam(value = "fundingFilter", required = false) Optional<FundingDecisionStatus> fundingFilter,
            @RequestParam(value = "inAssessmentReviewPanel", required = false) Optional<Boolean> inAssessmentReviewPanel) {
        return applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, sortBy, pageIndex, pageSize, filter, fundingFilter, inAssessmentReviewPanel).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetition/{competitionId}/not-submitted", "/find-by-competition/{competitionId}/not-submitted"})
    public RestResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(
            @PathVariable("competitionId") long competitionId,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int pageIndex,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(competitionId, sortBy, pageIndex, pageSize).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetition/{competitionId}/with-funding-decision", "/find-by-competition/{competitionId}/with-funding-decision"})
    public RestResult<ApplicationSummaryPageResource> getWithFundingDecisionApplicationSummariesByCompetitionId(
            @PathVariable("competitionId") long competitionId,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int pageIndex,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "filter", required = false) Optional<String> filter,
            @RequestParam(value = "sendFilter", required = false) Optional<Boolean> sendFilter,
            @RequestParam(value = "fundingFilter", required = false) Optional<FundingDecisionStatus> fundingFilter) {
        return applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, sortBy, pageIndex, pageSize, filter, sendFilter, fundingFilter).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping(value = {"/findByCompetition/{competitionId}/with-funding-decision", "/find-by-competition/{competitionId}/with-funding-decision"}, params = "all")
    public RestResult<List<Long>> getWithFundingDecisionApplicationSummariesByCompetitionId(
            @PathVariable("competitionId") long competitionId,
            @RequestParam(value = "filter", required = false) Optional<String> filter,
            @RequestParam(value = "sendFilter", required = false) Optional<Boolean> sendFilter,
            @RequestParam(value = "fundingFilter", required = false) Optional<FundingDecisionStatus> fundingFilter) {
        return applicationSummaryService.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(competitionId, filter, sendFilter, fundingFilter).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetition/{competitionId}/ineligible", "/find-by-competition/{competitionId}/ineligible"})
    public RestResult<ApplicationSummaryPageResource> getIneligibleApplicationsSummariesByCompetitionId(
            @PathVariable("competitionId") long competitionId,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int pageIndex,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "filter", required = false) Optional<String> filter,
            @RequestParam(value = "informFilter", required = false) Optional<Boolean> informFilter) {
        return applicationSummaryService.getIneligibleApplicationSummariesByCompetitionId(competitionId, sortBy, pageIndex, pageSize, filter, informFilter).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/applicationTeam/{applicationId}", "/application-team/{applicationId}"})
    public RestResult<ApplicationTeamResource> getApplicationTeamByApplicationId(@PathVariable("applicationId") long applicationId) {
        return applicationSummaryService.getApplicationTeamByApplicationId(applicationId).toGetResponse();
    }
}
