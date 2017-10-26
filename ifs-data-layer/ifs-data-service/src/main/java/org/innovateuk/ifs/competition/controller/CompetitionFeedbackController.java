package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CompetitionController exposes Competition data and operations through a REST API.
 */
@RestController
@RequestMapping("/competition/feedback")
public class CompetitionFeedbackController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessorService assessorService;

    @Autowired
    private ApplicationService applicationService;

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
}
