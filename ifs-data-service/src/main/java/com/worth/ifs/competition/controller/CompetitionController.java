package com.worth.ifs.competition.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
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

    /****
     * General Competition methods
     ****/

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RestResult<CompetitionResource> getCompetitionById(@PathVariable("id") final Long id) {
        return competitionService.getCompetitionById(id).toGetResponse();
    }

    @RequestMapping("/findAll")
    public RestResult<List<CompetitionResource>> findAll() {
        return competitionService.findAll().toGetResponse();
    }


    /***
     * Get methods for the competition dashboard.
     */
    @RequestMapping(value="/live", method= RequestMethod.GET)
    public RestResult<List<CompetitionResource>> live() {
        return competitionService.findLiveCompetitions().toGetResponse();
    }

    @RequestMapping(value="/projectSetup", method= RequestMethod.GET)
    public RestResult<List<CompetitionResource>> projectSetup() {
        return competitionService.findProjectSetupCompetitions().toGetResponse();
    }

    @RequestMapping(value="/upcoming", method= RequestMethod.GET)
    public RestResult<List<CompetitionResource>> upcoming() {
        return competitionService.findUpcomingCompetitions().toGetResponse();
    }

    @RequestMapping(value="/count", method= RequestMethod.GET)
    public RestResult<CompetitionCountResource> count() {
        return competitionService.countCompetitions().toGetResponse();
    }

    /****
     * Competition Setup methods
     ****/

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public RestResult<CompetitionResource> saveCompetition(@RequestBody CompetitionResource competitionResource, @PathVariable("id") final Long id) {
        return competitionSetupService.update(id, competitionResource).toGetResponse();
    }
    
    @RequestMapping(value = "/{id}/initialise-form/{competitionTypeId}", method = RequestMethod.POST)
    public RestResult<Void> initialiseForm(@PathVariable("id") Long competitionId, @PathVariable("competitionTypeId") Long competitionType) {
    	return competitionSetupService.initialiseFormForCompetitionType(competitionId, competitionType).toGetResponse();
    }

    /**
     * Generate and save the competition code
     */
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

    /**
     * Create a new competition object, and return it.
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public RestResult<CompetitionResource> create() {
        return competitionSetupService.create().toPostCreateResponse();
    }
}
