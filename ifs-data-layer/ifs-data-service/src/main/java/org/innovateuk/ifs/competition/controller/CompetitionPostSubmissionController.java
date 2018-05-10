package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.application.transactional.ApplicationNotificationService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling the competition after submission of the application phase
 */
@RestController
@RequestMapping("/competition/postSubmission")
public class CompetitionPostSubmissionController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "20";

    private static final String DEFAULT_SORT_BY = "id";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessorService assessorService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationNotificationService applicationNotificationService;

    @PutMapping("/{id}/notify-assessors")
    public RestResult<Void> notifyAssessors(@PathVariable("id") final long competitionId) {
        return competitionService.notifyAssessors(competitionId)
                .andOnSuccess(() -> assessorService.notifyAssessorsByCompetition(competitionId))
                .toPutResponse();
    }

    @PutMapping("/{id}/release-feedback")
    public RestResult<Void> releaseFeedback(@PathVariable("id") final long competitionId) {
        return competitionService.releaseFeedback(competitionId)
                .andOnSuccess(() -> applicationNotificationService.notifyApplicantsByCompetition(competitionId))
                .toPutResponse();
    }

    @GetMapping("/feedback-released")
    public RestResult<List<CompetitionSearchResultItem>> feedbackReleased() {
        return competitionService.findFeedbackReleasedCompetitions().toGetResponse();
    }

    @PutMapping("/{id}/close-assessment")
    public RestResult<Void> closeAssessment(@PathVariable("id") final Long id) {
        return competitionService.closeAssessment(id).toPutResponse();
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
    public RestResult<List<SpendProfileStatusResource>> getPendingSpendProfiles(@PathVariable(value = "competitionId") Long competitionId) {
        return competitionService.getPendingSpendProfiles(competitionId).toGetResponse();
    }

    @GetMapping("/{competitionId}/count-pending-spend-profiles")
    public RestResult<Long> countPendingSpendProfiles(@PathVariable(value = "competitionId") Long competitionId) {
        return competitionService.countPendingSpendProfiles(competitionId).toGetResponse();
    }
}
