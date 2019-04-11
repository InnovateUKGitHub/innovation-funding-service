package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.transactional.CompetitionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    private CompetitionTypeService competitionTypeService;

    @GetMapping("/findAll")
    public RestResult<List<CompetitionTypeResource>> findAllTypes() {
        return competitionTypeService.findAllTypes().toGetResponse();
    }
}
