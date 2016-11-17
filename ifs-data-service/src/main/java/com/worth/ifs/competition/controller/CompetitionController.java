package com.worth.ifs.competition.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CompetitionController exposes Competition data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private CompetitionSetupService competitionSetupService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RestResult<CompetitionResource> getCompetitionById(@PathVariable("id") final Long id) {
        return competitionService.getCompetitionById(id).toGetResponse();
    }

    @RequestMapping("/findAll")
    public RestResult<List<CompetitionResource>> findAll() {
        return competitionService.findAll().toGetResponse();
    }

    @RequestMapping(value="/live", method= RequestMethod.GET)
    public RestResult<List<CompetitionSearchResultItem>> live() {
        return competitionService.findLiveCompetitions().toGetResponse();
    }

    @RequestMapping(value="/projectSetup", method= RequestMethod.GET)
    public RestResult<List<CompetitionSearchResultItem>> projectSetup() {
        return competitionService.findProjectSetupCompetitions().toGetResponse();
    }

    @RequestMapping(value="/upcoming", method= RequestMethod.GET)
    public RestResult<List<CompetitionSearchResultItem>> upcoming() {
        return competitionService.findUpcomingCompetitions().toGetResponse();
    }

    @RequestMapping(value="/search/{page}/{size}", method= RequestMethod.GET)
    public RestResult<CompetitionSearchResult> search(@RequestParam("searchQuery") String searchQuery,
                                                      @PathVariable("page") int page,
                                                      @PathVariable("size") int size) {
        return competitionService.searchCompetitions(searchQuery, page, size).toGetResponse();
    }
    @RequestMapping(value="/count", method= RequestMethod.GET)
    public RestResult<CompetitionCountResource> count() {
        return competitionService.countCompetitions().toGetResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public RestResult<CompetitionResource> saveCompetition(@RequestBody CompetitionResource competitionResource, @PathVariable("id") final Long id) {
        return competitionSetupService.update(id, competitionResource).toGetResponse();
    }
    
    @RequestMapping(value = "/{id}/initialise-form/{competitionTypeId}", method = RequestMethod.POST)
    public RestResult<Void> initialiseForm(@PathVariable("id") Long competitionId, @PathVariable("competitionTypeId") Long competitionType) {
        return competitionSetupService.copyFromCompetitionTypeTemplate(competitionId, competitionType).toPostResponse();
    }


    @RequestMapping(value = "/generateCompetitionCode/{id}", method = RequestMethod.POST)
    public RestResult<String> generateCompetitionCode(@RequestBody LocalDateTime dateTime, @PathVariable("id") final Long id) {
        return competitionSetupService.generateCompetitionCode(id, dateTime).toGetResponse();
    }

    @RequestMapping("/sectionStatus/complete/{competitionId}/{section}")
    public RestResult<Void> markSectionComplete(@PathVariable("competitionId") final Long competitionId, @PathVariable("section") final CompetitionSetupSection section) {
        return competitionSetupService.markSectionComplete(competitionId, section).toGetResponse();
    }

    @RequestMapping("/sectionStatus/incomplete/{competitionId}/{section}")
    public RestResult<Void> markSectionInComplete(@PathVariable("competitionId") final Long competitionId, @PathVariable("section") final CompetitionSetupSection section) {
        return competitionSetupService.markSectionInComplete(competitionId, section).toGetResponse();
    }

    @RequestMapping(value = "/{id}/mark-as-setup", method = RequestMethod.POST)
    public RestResult<Void> markAsSetup(@PathVariable("id") final Long competitionId) {
        return competitionSetupService.markAsSetup(competitionId).toPostResponse();
    }
    @RequestMapping(value = "/{id}/return-to-setup", method = RequestMethod.POST)
    public RestResult<Void> returnToSetup(@PathVariable("id") final Long competitionId) {
        return competitionSetupService.returnToSetup(competitionId).toPostResponse();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public RestResult<CompetitionResource> create() {
        return competitionSetupService.create().toPostCreateResponse();
    }
}
