package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionThirdPartyConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/competition-third-party-config")
public class CompetitionThirdPartyConfigController {

    @Autowired
    private CompetitionThirdPartyConfigService competitionThirdPartyConfigService;

    @GetMapping("/{competitionId}")
    public RestResult<CompetitionThirdPartyConfigResource> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionThirdPartyConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }
}
