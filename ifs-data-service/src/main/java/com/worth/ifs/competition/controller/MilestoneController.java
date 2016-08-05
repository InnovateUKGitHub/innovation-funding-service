package com.worth.ifs.competition.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.transactional.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.worth.ifs.competition.resource.MilestoneResource.MilestoneName;

import java.util.List;

/**
 * MilestoneController exposes Milestone data and operations through a REST API
 */
@RestController
@RequestMapping("/milestone")
public class MilestoneController {

    @Autowired
    private MilestoneService milestoneService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RestResult<List<MilestoneResource>> getAllDatesByCompetitionId(
            @PathVariable("id") final Long id){
        return milestoneService.getAllDatesByCompetitionId(id).toGetResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public RestResult<MilestoneResource> create(@RequestBody final MilestoneName name,
                                                @PathVariable("id") final Long id) {
        return milestoneService.create(name, id).toPostCreateResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public RestResult<Void> saveMilestone(@RequestBody List<MilestoneResource> milestones, @PathVariable("id") final Long id) {
         return milestoneService.update(id, milestones).toPutResponse();
    }
 }
