package com.worth.ifs.competition.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.transactional.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MilestoneController exposes Milestone data and operations through a REST API
 */
@RestController
@RequestMapping("/milestone")
public class MilestoneController {

    @Autowired
    private MilestoneService milestoneService;

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public RestResult<List<MilestoneResource>> getAllDatesByCompetitionId(
            @PathVariable("competitionId") final Long competitionId){
        return milestoneService.getAllDatesByCompetitionId(competitionId).toGetResponse();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public RestResult<MilestoneResource> create() { return milestoneService.create().toPostCreateResponse();}

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public RestResult<ValidationMessages> saveMilestone(@RequestBody List<MilestoneResource> milestones, @PathVariable("id") final Long id) {
         return milestoneService.update(id, milestones).toPutWithBodyResponse();
    }
 }
