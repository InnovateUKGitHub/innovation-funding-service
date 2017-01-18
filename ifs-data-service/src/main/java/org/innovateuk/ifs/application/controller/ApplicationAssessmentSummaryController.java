package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Controller for accessing Application Assessment Summaries.
 */
@RestController
@RequestMapping("/applicationAssessmentSummary")
public class ApplicationAssessmentSummaryController {

    @Autowired
    private ApplicationAssessmentSummaryService applicationAssessmentSummaryService;

    @RequestMapping(value = "/{applicationId}/assessors", method = GET)
    public RestResult<List<ApplicationAssessorResource>> getAssessors(@PathVariable("applicationId") Long applicationId) {
        return applicationAssessmentSummaryService.getAssessors(applicationId).toGetResponse();
    }

    @RequestMapping(value = "/{applicationId}", method = GET)
    public RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(@PathVariable("applicationId") Long applicationId) {
        return applicationAssessmentSummaryService.getApplicationAssessmentSummary(applicationId).toGetResponse();
    }
}