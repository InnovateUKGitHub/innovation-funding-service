package com.worth.ifs.competition.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.transactional.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RestResult<CompetitionResource> getCompetitionById(@PathVariable("id") final Long id) {
        return competitionService.getCompetitionById(id).toGetResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public RestResult<CompetitionResource> saveCompetition(@RequestBody CompetitionResource competitionResource, @PathVariable("id") final Long id) {
        return competitionService.update(id, competitionResource).toGetResponse();
    }

    @RequestMapping("/findAll")
    public RestResult<List<CompetitionResource>> findAll() {
        return competitionService.findAll().toGetResponse();
    }

    /**
     * Create a new competition object, and return it.
     */
    @RequestMapping("/create")
    public RestResult<CompetitionResource> create() {
        return competitionService.create().toGetResponse();
    }
}
