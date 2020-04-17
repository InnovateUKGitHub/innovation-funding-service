package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionAverageAssessorScoreConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionAverageAssessorScoreConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/competition-average-assessor-score-config")
public class CompetitionAverageAssessorScoreConfigController {

    @Autowired
    private CompetitionAverageAssessorScoreConfigService competitionAverageAssessorScoreConfigService;

    @GetMapping("/find-by-competition-id/{competitionId}")
    public RestResult<CompetitionAverageAssessorScoreConfigResource> findOneByCompetitionId(@PathVariable final long competitionId) {
        return competitionAverageAssessorScoreConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }
}
