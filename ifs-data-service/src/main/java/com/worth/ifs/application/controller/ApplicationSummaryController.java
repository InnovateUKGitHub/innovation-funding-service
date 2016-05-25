package com.worth.ifs.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.transactional.ApplicationSummaryService;
import com.worth.ifs.application.transactional.CompetitionSummaryService;
import com.worth.ifs.commons.rest.RestResult;

/**
 * ApplicationSummaryController exposes application summary data and operations through a REST API.
 * It is mainly used at present for getting summaries of applications for showing in the competition manager views.
 */
@RestController
@RequestMapping("/applicationSummary")
public class ApplicationSummaryController {
	private static final String DEFAULT_PAGE_SIZE = "20";
	
    @Autowired
    private ApplicationSummaryService applicationSummaryService;
    
    @Autowired
    private CompetitionSummaryService competitionSummaryService;
	
    @RequestMapping("/findByCompetition/{competitionId}")
    public RestResult<ApplicationSummaryPageResource> getApplicationSummaryByCompetitionId(@PathVariable("competitionId") Long competitionId, @RequestParam(value="sort", required=false) String sortBy, @RequestParam(value="page", defaultValue="0") int pageIndex, @RequestParam(value="size", defaultValue=DEFAULT_PAGE_SIZE) int pageSize) {
        return applicationSummaryService.getApplicationSummariesByCompetitionId(competitionId, sortBy, pageIndex, pageSize).toGetResponse();
    }

    @RequestMapping("/getCompetitionSummary/{id}")
    public RestResult<CompetitionSummaryResource> getCompetitionSummary(@PathVariable("id") Long id) {
        return competitionSummaryService.getCompetitionSummaryByCompetitionId(id).toGetResponse();
    }
    
    @RequestMapping("/findByCompetition/{competitionId}/submitted")
    public RestResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(@PathVariable("competitionId") Long competitionId, @RequestParam(value="sort", required=false) String sortBy, @RequestParam(value="page", defaultValue="0") int pageIndex, @RequestParam(value="size", defaultValue=DEFAULT_PAGE_SIZE) int pageSize) {
        return applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(competitionId, sortBy, pageIndex, pageSize).toGetResponse();
    }
    
    @RequestMapping("/findByCompetition/{competitionId}/not-submitted")
    public RestResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(@PathVariable("competitionId") Long competitionId, @RequestParam(value="sort", required=false) String sortBy, @RequestParam(value="page", defaultValue="0") int pageIndex, @RequestParam(value="size", defaultValue=DEFAULT_PAGE_SIZE) int pageSize) {
        return applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(competitionId, sortBy, pageIndex, pageSize).toGetResponse();
    }
    
}
