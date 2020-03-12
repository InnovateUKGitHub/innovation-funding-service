package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for accessing Application Assessment Summaries.
 */
@RestController
@RequestMapping("/application-assessment-summary")
public class ApplicationAssessmentSummaryController {

    private static final String DEFAULT_PAGE_SIZE = "20";

    @Autowired
    private ApplicationAssessmentSummaryService applicationAssessmentSummaryService;

    @GetMapping("/{applicationId}/assigned-assessors")
    public RestResult<List<ApplicationAssessorResource>> getAssignedAssessors(@PathVariable long applicationId) {
        return applicationAssessmentSummaryService.getAssignedAssessors(applicationId).toGetResponse();
    }

    @GetMapping("/{applicationId}/available-assessors")
    public RestResult<ApplicationAvailableAssessorPageResource> getAvailableAssessors(@PathVariable long applicationId,
                                                                                      @RequestParam(value = "page", defaultValue = "0") int pageIndex,
                                                                                      @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                                                      @RequestParam(value = "assessorNameFilter", required = false) String assessorNameFilter,
                                                                                      @RequestParam(value = "sort", required = false, defaultValue = "ASSESSOR") ApplicationAvailableAssessorResource.Sort sort) {
        return applicationAssessmentSummaryService.getAvailableAssessors(applicationId, pageIndex, pageSize, assessorNameFilter, sort).toGetResponse();
    }

    @GetMapping("/{applicationId}/available-assessors-ids")
    public RestResult<List<Long>> getAvailableAssessorIds(@PathVariable long applicationId,
                                                          @RequestParam(value = "assessorNameFilter", required = false) String assessorNameFilter) {
        return applicationAssessmentSummaryService.getAvailableAssessorIds(applicationId, assessorNameFilter).toGetResponse();
    }

    @GetMapping("/{applicationId}")
    public RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(@PathVariable long applicationId) {
        return applicationAssessmentSummaryService.getApplicationAssessmentSummary(applicationId).toGetResponse();
    }
}