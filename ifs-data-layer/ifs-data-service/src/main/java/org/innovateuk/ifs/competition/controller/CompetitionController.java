package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionPendingSpendProfilesResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
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
    public RestResult<CompetitionResource> getCompetitionById(@PathVariable("id") final Long id) {
        return competitionService.getCompetitionById(id).toGetResponse();
    }

    @GetMapping("/{id}/innovation-leads")
    public RestResult<List<UserResource>> findInnovationLeads(@PathVariable("id") final Long competitionId) {

        return competitionService.findInnovationLeads(competitionId).toGetResponse();
    }

    @PostMapping("/{id}/add-innovation-lead/{innovationLeadUserId}")
    public RestResult<Void> addInnovationLead(@PathVariable("id") final Long competitionId,
                                              @PathVariable("innovationLeadUserId") final Long innovationLeadUserId) {

        return competitionService.addInnovationLead(competitionId, innovationLeadUserId).toPostResponse();
    }

    @PostMapping("/{id}/remove-innovation-lead/{innovationLeadUserId}")
    public RestResult<Void> removeInnovationLead(@PathVariable("id") final Long competitionId,
                                                 @PathVariable("innovationLeadUserId") final Long innovationLeadUserId) {

        return competitionService.removeInnovationLead(competitionId, innovationLeadUserId).toPostResponse();
    }

    @GetMapping("/{id}/getOrganisationTypes")
    public RestResult<List<OrganisationTypeResource>> getOrganisationTypes(@PathVariable("id") final Long id) {
        return competitionService.getCompetitionOrganisationTypes(id).toGetResponse();
    }

    @GetMapping("/getCompetitionsByUserId/{userId}")
    public RestResult<List<CompetitionResource>> getCompetitionsByUserId(@PathVariable("userId") final Long userId) {
        return competitionService.getCompetitionsByUserId(userId).toGetResponse();
    }

    @GetMapping("/findAll")
    public RestResult<List<CompetitionResource>> findAll() {
        return competitionService.findAll().toGetResponse();
    }

    @GetMapping("/live")
    public RestResult<List<CompetitionSearchResultItem>> live() {
        return competitionService.findLiveCompetitions().toGetResponse();
    }

    @GetMapping("/project-setup")
    public RestResult<List<CompetitionSearchResultItem>> projectSetup() {
        return competitionService.findProjectSetupCompetitions().toGetResponse();
    }

    @GetMapping("/upcoming")
    public RestResult<List<CompetitionSearchResultItem>> upcoming() {
        return competitionService.findUpcomingCompetitions().toGetResponse();
    }

    @GetMapping("/non-ifs")
    public RestResult<List<CompetitionSearchResultItem>> nonIfs() {
        return competitionService.findNonIfsCompetitions().toGetResponse();
    }

    @GetMapping("/search/{page}/{size}")
    public RestResult<CompetitionSearchResult> search(@RequestParam("searchQuery") String searchQuery,
                                                      @PathVariable("page") int page,
                                                      @PathVariable("size") int size) {
        return competitionService.searchCompetitions(searchQuery, page, size).toGetResponse();
    }

    @GetMapping("/count")
    public RestResult<CompetitionCountResource> count() {
        return competitionService.countCompetitions().toGetResponse();
    }

    @GetMapping("/{competitionId}/pending-spend-profiles")
    public RestResult<List<CompetitionPendingSpendProfilesResource>> getPendingSpendProfiles(@PathVariable(value = "competitionId") Long competitionId) {
        return competitionService.getPendingSpendProfiles(competitionId).toGetResponse();
    }

    @GetMapping("/{competitionId}/count-pending-spend-profiles")
    public RestResult<Integer> countPendingSpendProfiles(@PathVariable(value = "competitionId") Long competitionId) {
        return competitionService.countPendingSpendProfiles(competitionId).toGetResponse();
    }
}
