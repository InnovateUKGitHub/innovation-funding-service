package com.worth.ifs.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.transactional.ApplicationSummaryService;
import com.worth.ifs.commons.rest.RestResult;

/**
 * ApplicationSummaryController exposes application summary data and operations through a REST API.
 * It is mainly used at present for getting summaries of applications for showing in the competition manager views.
 */
@RestController
@RequestMapping("/applicationSummary")
public class ApplicationSummaryController {
    @Autowired
    private ApplicationSummaryService applicationSummaryService;
	
    @RequestMapping("/findByCompetition/{competitionId}")
    public RestResult<ApplicationSummaryPageResource> getApplicationSummaryByCompetitionId(@PathVariable("competitionId") Long competitionId, @RequestParam(value="page", defaultValue="0") int pageIndex, @RequestParam(value="sort", required=false) String sortBy) {
        return applicationSummaryService.getApplicationSummariesByCompetitionId(competitionId, pageIndex, sortBy).toGetResponse();
    }

    @RequestMapping("/getCompetitionSummary/{id}")
    public RestResult<CompetitionSummaryResource> getCompetitionSummary(@PathVariable("id") Long id) {
        return applicationSummaryService.getCompetitionSummaryByCompetitionId(id).toGetResponse();
    }
    
    @RequestMapping("/findByClosedCompetition/{competitionId}/submitted")
    public RestResult<ClosedCompetitionSubmittedApplicationSummaryPageResource> getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(@PathVariable("competitionId") Long competitionId, @RequestParam(value="page", defaultValue="0") int pageIndex, @RequestParam(value="sort", required=false) String sortBy) {
        return applicationSummaryService.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(competitionId, pageIndex, sortBy).toGetResponse();
    }
    
    @RequestMapping("/findByClosedCompetition/{competitionId}/not-submitted")
    public RestResult<ClosedCompetitionNotSubmittedApplicationSummaryPageResource> getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(@PathVariable("competitionId") Long competitionId, @RequestParam(value="page", defaultValue="0") int pageIndex, @RequestParam(value="sort", required=false) String sortBy) {
        return applicationSummaryService.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(competitionId, pageIndex, sortBy).toGetResponse();
    }
    
}
