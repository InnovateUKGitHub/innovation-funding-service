package com.worth.ifs.competition.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
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


    /****
     * Competition Setup methods
     ****/

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public RestResult<CompetitionResource> saveCompetition(@RequestBody CompetitionResource competitionResource, @PathVariable("id") final Long id) {
        return competitionSetupService.update(id, competitionResource).toGetResponse();
    }

    /**
     * Generate and save the competition code
     */
    @RequestMapping(value = "/generateCompetitionCode/{id}", method = RequestMethod.POST)
    public RestResult<String> generateCompetitionCode(@RequestBody LocalDateTime dateTime, @PathVariable("id") final Long id) {
        return competitionSetupService.generateCompetitionCode(id, dateTime).toGetResponse();
    }

    @RequestMapping("/types/findAll")
    public RestResult<List<CompetitionTypeResource>> findAllTypes() {
        return competitionSetupService.findAllTypes().toGetResponse();
    }


    @RequestMapping("/sectionStatus/find/{competitionId}")
    public RestResult<List<CompetitionSetupCompletedSectionResource>> findAllCompetitionSection(@PathVariable("competitionId") final Long competitionId) {
        return competitionSetupService.findAllCompetitionSectionsStatuses(competitionId).toGetResponse();
    }

    @RequestMapping("/sectionStatus/complete/{competitionId}/{sectionId}")
    public RestResult<Void> markSectionComplete(@PathVariable("competitionId") final Long competitionId, @PathVariable("sectionId") final Long sectionId) {
        return competitionSetupService.markSectionComplete(competitionId, sectionId).toGetResponse();
    }

    @RequestMapping("/sectionStatus/incomplete/{competitionId}/{sectionId}")
    public RestResult<Void> markSectionInComplete(@PathVariable("competitionId") final Long competitionId, @PathVariable("sectionId") final Long sectionId) {
        return competitionSetupService.markSectionInComplete(competitionId, sectionId).toGetResponse();
    }

    @RequestMapping("/sections/getAll")
    public RestResult<List<CompetitionSetupSectionResource>> findAllCompetitionSections() {
        return competitionSetupService.findAllCompetitionSections().toGetResponse();
    }

    /**
     * Create a new competition object, and return it.
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public RestResult<CompetitionResource> create() {
        return competitionSetupService.create().toPostCreateResponse();
    }
}
