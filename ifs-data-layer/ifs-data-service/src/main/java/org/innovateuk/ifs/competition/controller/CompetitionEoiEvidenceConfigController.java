package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/competition")
public class CompetitionEoiEvidenceConfigController {

    @Autowired
    private CompetitionEoiEvidenceConfigService competitionEoiEvidenceConfigService;

    @GetMapping("/{applicationId}/eoi-evidence-config")
    public RestResult<CompetitionEoiEvidenceConfigResource> findOneByApplicationId(@PathVariable("competitionId") long competitionId) {
        return competitionEoiEvidenceConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }
}
