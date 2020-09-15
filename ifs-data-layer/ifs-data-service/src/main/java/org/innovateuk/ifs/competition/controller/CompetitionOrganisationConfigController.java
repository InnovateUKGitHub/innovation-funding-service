package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionOrganisationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/competition-organisation-config")
public class CompetitionOrganisationConfigController {

    @Autowired
    private CompetitionOrganisationConfigService competitionOrganisationConfigService;

    @GetMapping("/find-by-competition-id/{competitionId}")
    public RestResult<CompetitionOrganisationConfigResource> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionOrganisationConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }

    @PutMapping("/update/{competitionId}")
    public RestResult<Void> update(@PathVariable final long competitionId, @RequestBody CompetitionOrganisationConfigResource competitionOrganisationConfigResource) {
        return competitionOrganisationConfigService.update(competitionId, competitionOrganisationConfigResource).toPutResponse();
    }
}
