package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.application.transactional.ApplicationNotificationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling the competition after submission of the application phase
 */
@RestController
@RequestMapping("/competition/post-submission")
public class CompetitionPostSubmissionController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationNotificationService applicationNotificationService;

    @PutMapping("/{id}/release-feedback")
    public RestResult<Void> releaseFeedback(@PathVariable("id") final long competitionId) {
        return competitionService.releaseFeedback(competitionId)
                .andOnSuccess(() -> applicationNotificationService.notifyApplicantsByCompetition(competitionId))
                .toPutResponse();
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
