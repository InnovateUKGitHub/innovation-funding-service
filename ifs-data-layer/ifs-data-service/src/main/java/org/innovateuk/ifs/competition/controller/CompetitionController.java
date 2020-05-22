package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CompetitionController exposes Competition data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @GetMapping("/{id}")
    public RestResult<CompetitionResource> getCompetitionById(@PathVariable("id") final long id) {
        return competitionService.getCompetitionById(id).toGetResponse();
    }

    @GetMapping("/{id}/get-organisation-types")
    public RestResult<List<OrganisationTypeResource>> getOrganisationTypes(@PathVariable("id") final long id) {
        return competitionService.getCompetitionOrganisationTypes(id).toGetResponse();
    }

    @GetMapping("/find-all")
    public RestResult<List<CompetitionResource>> findAll() {
        return competitionService.findAll().toGetResponse();
    }
    
    @PutMapping("{id}/update-terms-and-conditions/{tcId}")
    public RestResult<Void> updateTermsAndConditionsForCompetition(@PathVariable("id") final long competitionId,
                                                                   @PathVariable("tcId") final long termsAndConditionsId) {
        return competitionService.updateTermsAndConditionsForCompetition(competitionId, termsAndConditionsId).toPutResponse();
    }

    @GetMapping(path = "/{id}/terms-and-conditions", produces = "application/json")
    public ResponseEntity<Object> downloadTerms(@PathVariable("id") long competitionId) {
        return fileControllerUtils.handleFileDownload(() -> competitionService.downloadTerms(competitionId));
    }
}