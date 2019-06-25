package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.transactional.CompetitionSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CompetitionController exposes Competition data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition")
public class CompetitionSearchController {

    @Autowired
    private CompetitionSearchService competitionSearchService;

    @GetMapping("/live")
    public RestResult<List<CompetitionSearchResultItem>> live() {
        return competitionSearchService.findLiveCompetitions().toGetResponse();
    }

    @GetMapping(value = "/project-setup")
    public RestResult<CompetitionSearchResult> projectSetup(@RequestParam int page,
                                                            @RequestParam(required = false, defaultValue = "20") int size) {
        return competitionSearchService.findProjectSetupCompetitions(page, size).toGetResponse();
    }

    @GetMapping("/upcoming")
    public RestResult<List<CompetitionSearchResultItem>> upcoming() {
        return competitionSearchService.findUpcomingCompetitions().toGetResponse();
    }

    @GetMapping(value = "/non-ifs")
    public RestResult<CompetitionSearchResult> nonIfs(@RequestParam int page,
                                                      @RequestParam(required = false, defaultValue = "20") int size) {
        return competitionSearchService.findNonIfsCompetitions(page, size).toGetResponse();
    }

    @GetMapping(value = "/post-submission/feedback-released")
    public RestResult<CompetitionSearchResult> previous(@RequestParam int page,
                                                        @RequestParam(required = false, defaultValue = "20") int size) {
        return competitionSearchService.findPreviousCompetitions(page, size).toGetResponse();
    }

    @GetMapping("/search")
    public RestResult<CompetitionSearchResult> search(@RequestParam String searchQuery,
                                                      @RequestParam int page,
                                                      @RequestParam(required = false, defaultValue = "20") int size) {
        return competitionSearchService.searchCompetitions(searchQuery, page, size).toGetResponse();
    }

    @GetMapping("/count")
    public RestResult<CompetitionCountResource> count() {
        return competitionSearchService.countCompetitions().toGetResponse();
    }
}
