package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.transactional.CompetitionSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/project-setup")
    public RestResult<List<CompetitionSearchResultItem>> projectSetup() {
        return competitionSearchService.findProjectSetupCompetitions().toGetResponse();
    }

    @GetMapping("/upcoming")
    public RestResult<List<CompetitionSearchResultItem>> upcoming() {
        return competitionSearchService.findUpcomingCompetitions().toGetResponse();
    }

    @GetMapping("/non-ifs")
    public RestResult<List<CompetitionSearchResultItem>> nonIfs() {
        return competitionSearchService.findNonIfsCompetitions().toGetResponse();
    }

    @GetMapping("/post-submission/feedback-released")
    public RestResult<List<CompetitionSearchResultItem>> previous() {
        return competitionSearchService.findPreviousCompetitions().toGetResponse();
    }

    @GetMapping("/search/{page}/{size}")
    public RestResult<CompetitionSearchResult> search(@RequestParam("searchQuery") String searchQuery,
                                                      @PathVariable("page") int page,
                                                      @PathVariable("size") int size) {
        return competitionSearchService.searchCompetitions(searchQuery, page, size).toGetResponse();
    }


    @GetMapping("/count")
    public RestResult<CompetitionCountResource> count() {
        return competitionSearchService.countCompetitions().toGetResponse();
    }
}
