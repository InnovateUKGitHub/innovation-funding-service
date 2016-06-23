package com.worth.ifs.competition.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CompetitionTypeController exposes Competition types data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition-type")
public class CompetitionTypeController {

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @RequestMapping("/findAll")
    public RestResult<List<CompetitionTypeResource>> findAllTypes() {
        return competitionSetupService.findAllTypes().toGetResponse();
    }
}
