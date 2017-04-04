package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * CompetitionController exposes Competition data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private CompetitionSetupService competitionSetupService;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private ApplicationService applicationService;

    @GetMapping(value = "/{id}")
    public RestResult<CompetitionResource> getCompetitionById(@PathVariable("id") final Long id) {
        return competitionService.getCompetitionById(id).toGetResponse();
    }

    @GetMapping("/findAll")
    public RestResult<List<CompetitionResource>> findAll() {
        return competitionService.findAll().toGetResponse();
    }

    @GetMapping(value = "/live")
    public RestResult<List<CompetitionSearchResultItem>> live() {
        return competitionService.findLiveCompetitions().toGetResponse();
    }

    @GetMapping(value = "/project-setup")
    public RestResult<List<CompetitionSearchResultItem>> projectSetup() {
        return competitionService.findProjectSetupCompetitions().toGetResponse();
    }

    @GetMapping(value = "/upcoming")
    public RestResult<List<CompetitionSearchResultItem>> upcoming() {
        return competitionService.findUpcomingCompetitions().toGetResponse();
    }

    @GetMapping(value = "/non-ifs")
    public RestResult<List<CompetitionSearchResultItem>> nonIfs() {
        return competitionService.findNonIfsCompetitions().toGetResponse();
    }

    @GetMapping(value = "/search/{page}/{size}")
    public RestResult<CompetitionSearchResult> search(@RequestParam("searchQuery") String searchQuery,
                                                      @PathVariable("page") int page,
                                                      @PathVariable("size") int size) {
        return competitionService.searchCompetitions(searchQuery, page, size).toGetResponse();
    }

    @GetMapping(value = "/count")
    public RestResult<CompetitionCountResource> count() {
        return competitionService.countCompetitions().toGetResponse();
    }

    @PutMapping(value = "/{id}")
    public RestResult<CompetitionResource> saveCompetition(@RequestBody CompetitionResource competitionResource,
                                                           @PathVariable("id") final Long id) {
        return competitionSetupService.update(id, competitionResource).toGetResponse();
    }

    @PutMapping(value = "/{id}/close-assessment")
    public RestResult<Void> closeAssessment(@PathVariable("id") final Long id) {
        return competitionService.closeAssessment(id).toPutResponse();
    }

    @PostMapping(value = "/{id}/initialise-form/{competitionTypeId}")
    public RestResult<Void> initialiseForm(@PathVariable("id") Long competitionId,
                                           @PathVariable("competitionTypeId") Long competitionType) {
        return competitionSetupService.copyFromCompetitionTypeTemplate(competitionId, competitionType).toPostResponse();
    }


    @PostMapping(value = "/generateCompetitionCode/{id}")
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

    @PostMapping(value = "/{id}/mark-as-setup")
    public RestResult<Void> markAsSetup(@PathVariable("id") final Long competitionId) {
        return competitionSetupService.markAsSetup(competitionId).toPostResponse();
    }

    @PostMapping(value = "/{id}/return-to-setup")
    public RestResult<Void> returnToSetup(@PathVariable("id") final Long competitionId) {
        return competitionSetupService.returnToSetup(competitionId).toPostResponse();
    }

    @PostMapping(value = "")
    public RestResult<CompetitionResource> create() {
        return competitionSetupService.create().toPostCreateResponse();
    }


    @PostMapping(value = "/non-ifs")
    public RestResult<CompetitionResource> createNonIfs() {
        return competitionSetupService.createNonIfs().toPostCreateResponse();
    }


    @PutMapping(value = "/{id}/notify-assessors")
    public RestResult<Void> notifyAssessors(@PathVariable("id") final long competitionId) {
        return competitionService.notifyAssessors(competitionId)
                .andOnSuccess(() -> assessmentService.notifyAssessorsByCompetition(competitionId))
                .toPutResponse();
    }

    @PutMapping(value = "/{id}/release-feedback")
    public RestResult<Void> releaseFeedback(@PathVariable("id") final long competitionId) {
        return competitionService.releaseFeedback(competitionId)
                .andOnSuccess(() -> applicationService.notifyApplicantsByCompetition(competitionId))
                .toPutResponse();
    }
}
