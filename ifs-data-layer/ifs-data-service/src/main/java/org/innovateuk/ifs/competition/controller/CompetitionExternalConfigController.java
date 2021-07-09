package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionExternalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/competition-external-config")
public class CompetitionExternalConfigController {

    @Autowired
    private CompetitionExternalConfigService competitionExternalConfigService;

    @GetMapping("/{competitionId}")
    public RestResult<CompetitionExternalConfigResource> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionExternalConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }

    @PutMapping("/{competitionId}")
    public RestResult<Void> update(@PathVariable final long competitionId, @RequestBody CompetitionExternalConfigResource competitionExternalConfigResource) {
        return competitionExternalConfigService.update(competitionId, competitionExternalConfigResource).toPutResponse();
    }

}