package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.transactional.ApplicationCountSummaryService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for exposing statistical data on applications
 */
@RestController
@RequestMapping("/applicationCountSummary")
public class ApplicationCountSummaryController {

    @Autowired
    private ApplicationCountSummaryService applicationCountSummaryService;

    @RequestMapping("/findByCompetitionId/{competitionId}")
    public RestResult<List<ApplicationCountSummaryResource>> getApplicationCountSummariesByCompetitionId(@PathVariable("competitionId") Long competitionId) {
        return applicationCountSummaryService.getApplicationCountSummariesByCompetitionId(competitionId).toGetResponse();
    }
}
