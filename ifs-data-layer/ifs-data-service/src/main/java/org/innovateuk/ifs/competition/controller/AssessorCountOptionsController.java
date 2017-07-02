package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.transactional.AssessorCountOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AssessorCountOptionsController for AssessorCountOption data and operations through a REST API.
 */
@RestController
@RequestMapping("/assessor-count-options")
public class AssessorCountOptionsController {

    @Autowired
    private AssessorCountOptionService assessorCountOptionService;

    @GetMapping("/{competitionTypeId}")
    public RestResult<List<AssessorCountOptionResource>> getAllByCompetitionType(
            @PathVariable("competitionTypeId") Long competitionTypeId) {
        return assessorCountOptionService.findAllByCompetitionType(competitionTypeId).toGetResponse();
    }
}
