package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionOrganisationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/competition-organisation-config")
public class CompetitionOrganisationConfigController {

    @Autowired
    private CompetitionOrganisationConfigService competitionOrganisationConfigService;

    @GetMapping("/find-by-competition-id/{competitionId}")
    public RestResult<Optional<CompetitionOrganisationConfigResource>> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionOrganisationConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }
}
