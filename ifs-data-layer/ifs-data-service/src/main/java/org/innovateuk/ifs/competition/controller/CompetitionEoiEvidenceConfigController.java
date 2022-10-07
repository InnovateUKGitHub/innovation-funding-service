package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CompetitionEoiEvidenceConfigController {

    @Autowired
    private CompetitionEoiEvidenceConfigService competitionEoiEvidenceConfigService;

    @GetMapping("/competition/{competitionId}/eoi-evidence-config")
    public RestResult<CompetitionEoiEvidenceConfigResource> findByCompetitionId(@PathVariable("competitionId") long competitionId) {
        return competitionEoiEvidenceConfigService.findOneByCompetitionId(competitionId).toGetResponse();
    }

    @GetMapping("/competition-valid-file-type-ids/{competitionEoiEvidenceConfigId}")
    public RestResult<List<Long>> getValidFileTypesIdsForEoiEvidence(@PathVariable long competitionEoiEvidenceConfigId) {
        return competitionEoiEvidenceConfigService.getValidFileTypesIdsForEoiEvidence(competitionEoiEvidenceConfigId).toGetResponse();
    }
}
