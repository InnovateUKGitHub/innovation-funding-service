package org.innovateuk.ifs.competition.controller;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionApplicationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/competition-application-config")
public class CompetitionApplicationConfigController {

    @Autowired
    private CompetitionApplicationConfigService competitionApplicationConfigService;

    @GetMapping("/{competitionId}")
    public RestResult<CompetitionApplicationConfigResource> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionApplicationConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }

}
