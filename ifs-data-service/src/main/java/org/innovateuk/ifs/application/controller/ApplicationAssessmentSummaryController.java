package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for accessing Application Assessment Summaries.
 */
@RestController
@RequestMapping("/applicationAssessmentSummary")
public class ApplicationAssessmentSummaryController {

    private static final String DEFAULT_PAGE_SIZE = "20";

    @Autowired
    private ApplicationAssessmentSummaryService applicationAssessmentSummaryService;

    @GetMapping(value = "/{applicationId}/assignedAssessors")
    public RestResult<List<ApplicationAssessorResource>> getAssignedAssessors(@PathVariable("applicationId") Long applicationId) {
        return applicationAssessmentSummaryService.getAssignedAssessors(applicationId).toGetResponse();
    }

    @GetMapping(value = "/{applicationId}/availableAssessors")
    public RestResult<ApplicationAssessorPageResource> getAvailableAssessors(@PathVariable("applicationId") Long applicationId,
                                                                             @RequestParam(value = "page", defaultValue = "0") int pageIndex,
                                                                             @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                                             @RequestParam(value = "filterInnovationArea", required = false) Long filterInnovationArea) {
        return applicationAssessmentSummaryService.getAvailableAssessors(applicationId, pageIndex, pageSize, filterInnovationArea).toGetResponse();
    }

    @GetMapping("/{applicationId}")
    public RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(@PathVariable("applicationId") Long applicationId) {
        return applicationAssessmentSummaryService.getApplicationAssessmentSummary(applicationId).toGetResponse();
    }
}