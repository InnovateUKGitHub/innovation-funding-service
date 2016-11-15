package com.worth.ifs.competition.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;
import com.worth.ifs.competition.transactional.CompetitionTypeAssessorOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CompetitionTypeAssessorOptionsController for CompetitionTypeAssessorOption data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition-type-assessor-options")
public class CompetitionTypeAssessorOptionsController {

    @Autowired
    private CompetitionTypeAssessorOptionService competitionTypeAssessorOptionService;

    @RequestMapping("/{competitionTypeId}")
    public RestResult<List<CompetitionTypeAssessorOptionResource>> getAllByCompetitionType(@PathVariable("competitionTypeId") final Long competitionTypeId) {
        return competitionTypeAssessorOptionService.findAllByCompetitionType(competitionTypeId).toGetResponse();
    }
}
