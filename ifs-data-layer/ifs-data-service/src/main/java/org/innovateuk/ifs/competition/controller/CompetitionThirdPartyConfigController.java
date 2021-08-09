package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionThirdPartyConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/competition-third-party-config")
public class CompetitionThirdPartyConfigController {

    @Autowired
    private CompetitionThirdPartyConfigService competitionThirdPartyConfigService;

    @PostMapping
    public RestResult<CompetitionThirdPartyConfigResource> create(@RequestBody CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource) {
        return competitionThirdPartyConfigService.create(competitionThirdPartyConfigResource).toPostCreateResponse();
    }

    @GetMapping("/{competitionId}")
    public RestResult<CompetitionThirdPartyConfigResource> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionThirdPartyConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }

    @PutMapping("/{competitionId}")
    public RestResult<Void> update(@PathVariable final long competitionId, @RequestBody CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource) {
        return competitionThirdPartyConfigService.update(competitionId, competitionThirdPartyConfigResource).toPutResponse();
    }
}