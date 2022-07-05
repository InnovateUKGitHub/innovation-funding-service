package org.innovateuk.ifs.competition.controller;


import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionApplicationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/competition-application-config")
public class CompetitionApplicationConfigController {

    @Autowired
    private CompetitionApplicationConfigService competitionApplicationConfigService;

    @GetMapping("/{competitionId}")
    public RestResult<CompetitionApplicationConfigResource> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionApplicationConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }

    @PutMapping("/{competitionId}")
    public RestResult<Void> update(@PathVariable final long competitionId, @RequestBody CompetitionApplicationConfigResource competitionApplicationConfigResource) {
        return competitionApplicationConfigService.update(competitionId, competitionApplicationConfigResource).toPutResponse();
    }
}
