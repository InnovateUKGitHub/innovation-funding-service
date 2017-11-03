package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * CompetitionController exposes Competition data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "20";

    private static final String DEFAULT_SORT_BY = "id";

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private CompetitionSetupService competitionSetupService;
    @Autowired
    private AssessorService assessorService;
    @Autowired
    private ApplicationService applicationService;

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

    @GetMapping("/{competitionId}/unsuccessful-applications")
    public RestResult<ApplicationPageResource> findUnsuccessfulApplications(@PathVariable("competitionId") final Long competitionId,
                                                                            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                                            @RequestParam(value = "sort", defaultValue = DEFAULT_SORT_BY) String sortField) {

        return competitionService.findUnsuccessfulApplications(competitionId, pageIndex, pageSize, sortField).toGetResponse();
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

    @PutMapping("/{id}")
    public RestResult<CompetitionResource> saveCompetition(@RequestBody CompetitionResource competitionResource,
                                                           @PathVariable("id") final Long id) {
        return competitionSetupService.update(id, competitionResource).toGetResponse();
    }

    @PutMapping("/{id}/update-competition-initial-details")
    public RestResult<Void> updateCompetitionInitialDetails(@RequestBody CompetitionResource competitionResource,
                                                           @PathVariable("id") final Long id) {

        CompetitionResource existingCompetitionResource = competitionService.getCompetitionById(id).getSuccessObjectOrThrowException();
        return competitionSetupService.updateCompetitionInitialDetails(id, competitionResource, existingCompetitionResource.getLeadTechnologist()).toPutResponse();
    }

    @PutMapping("/{id}/close-assessment")
    public RestResult<Void> closeAssessment(@PathVariable("id") final Long id) {
        return competitionService.closeAssessment(id).toPutResponse();
    }

    @PostMapping("/{id}/initialise-form/{competitionTypeId}")
    public RestResult<Void> initialiseForm(@PathVariable("id") Long competitionId,
                                           @PathVariable("competitionTypeId") Long competitionType) {
        return competitionSetupService.copyFromCompetitionTypeTemplate(competitionId, competitionType).toPostResponse();
    }


    @PostMapping("/generateCompetitionCode/{id}")
    public RestResult<String> generateCompetitionCode(@RequestBody ZonedDateTime dateTime, @PathVariable("id") final Long id) {
        return competitionSetupService.generateCompetitionCode(id, dateTime).toGetResponse();
    }

    @GetMapping("/sectionStatus/complete/{competitionId}/{section}")
    public RestResult<Void> markSectionComplete(@PathVariable("competitionId") final Long competitionId,
                                                @PathVariable("section") final CompetitionSetupSection section) {
        return competitionSetupService.markSectionComplete(competitionId, section).toGetResponse();
    }

    @GetMapping("/sectionStatus/incomplete/{competitionId}/{section}")
    public RestResult<Void> markSectionInComplete(@PathVariable("competitionId") final Long competitionId,
                                                  @PathVariable("section") final CompetitionSetupSection section) {
        return competitionSetupService.markSectionInComplete(competitionId, section).toGetResponse();
    }

    @PostMapping("/{id}/mark-as-setup")
    public RestResult<Void> markAsSetup(@PathVariable("id") final Long competitionId) {
        return competitionSetupService.markAsSetup(competitionId).toPostResponse();
    }

    @PostMapping("/{id}/return-to-setup")
    public RestResult<Void> returnToSetup(@PathVariable("id") final Long competitionId) {
        return competitionSetupService.returnToSetup(competitionId).toPostResponse();
    }

    @PostMapping
    public RestResult<CompetitionResource> create() {
        return competitionSetupService.create().toPostCreateResponse();
    }


    @PostMapping("/non-ifs")
    public RestResult<CompetitionResource> createNonIfs() {
        return competitionSetupService.createNonIfs().toPostCreateResponse();
    }


    @PutMapping("/{id}/notify-assessors")
    public RestResult<Void> notifyAssessors(@PathVariable("id") final long competitionId) {
        return competitionService.notifyAssessors(competitionId)
                .andOnSuccess(() -> assessorService.notifyAssessorsByCompetition(competitionId))
                .toPutResponse();
    }

    @PutMapping("/{id}/release-feedback")
    public RestResult<Void> releaseFeedback(@PathVariable("id") final long competitionId) {
        return competitionService.releaseFeedback(competitionId)
                .andOnSuccess(() -> applicationService.notifyApplicantsByCompetition(competitionId))
                .toPutResponse();
    }

    @GetMapping("/feedback-released")
    public RestResult<List<CompetitionSearchResultItem>> feedbackReleased() {
        return competitionService.findFeedbackReleasedCompetitions().toGetResponse();
    }

    @GetMapping("/{id}/queries/open")
    public RestResult<List<CompetitionOpenQueryResource>> getOpenQueries(@PathVariable("id") Long competitionId) {
        return competitionService.findAllOpenQueries(competitionId).toGetResponse();
    }

    @GetMapping("/{id}/queries/open/count")
    public RestResult<Long> countOpenQueries(@PathVariable("id") Long competitionId) {
        return competitionService.countAllOpenQueries(competitionId).toGetResponse();
    }

    @GetMapping("/{competitionId}/pending-spend-profiles")
    public RestResult<List<CompetitionPendingSpendProfilesResource>> getPendingSpendProfiles(@PathVariable(value = "competitionId") Long competitionId) {
        return competitionService.getPendingSpendProfiles(competitionId).toGetResponse();
    }

}
