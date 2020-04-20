package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionAssessmentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/competition-assessment-config")
public class CompetitionAssessmentConfigController {

    @Autowired
    private CompetitionAssessmentConfigService competitionAssessmentConfigService;

    @GetMapping("/find-by-competition-id/{competitionId}")
    public RestResult<CompetitionAssessmentConfigResource> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionAssessmentConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }

    @PutMapping("/{competitionId}")
    public RestResult<Void> update(@PathVariable final long competitionId, @RequestBody CompetitionAssessmentConfigResource competitionAssessmentConfigResource) {
        return competitionAssessmentConfigService.update(competitionId, competitionAssessmentConfigResource).toPutResponse();
    }
}
