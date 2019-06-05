package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{id}")
    public RestResult<CompetitionResource> getCompetitionById(@PathVariable("id") final long id) {
        return competitionService.getCompetitionById(id).toGetResponse();
    }

    @GetMapping("/{id}/innovation-leads")
    public RestResult<List<UserResource>> findInnovationLeads(@PathVariable("id") final long competitionId) {

        return competitionService.findInnovationLeads(competitionId).toGetResponse();
    }

    @PostMapping("/{id}/add-innovation-lead/{innovationLeadUserId}")
    public RestResult<Void> addInnovationLead(@PathVariable("id") final long competitionId,
                                              @PathVariable("innovationLeadUserId") final long innovationLeadUserId) {

        return competitionService.addInnovationLead(competitionId, innovationLeadUserId).toPostResponse();
    }

    @PostMapping("/{id}/remove-innovation-lead/{innovationLeadUserId}")
    public RestResult<Void> removeInnovationLead(@PathVariable("id") final long competitionId,
                                                 @PathVariable("innovationLeadUserId") final long innovationLeadUserId) {

        return competitionService.removeInnovationLead(competitionId, innovationLeadUserId).toPostResponse();
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
}
