package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
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
 * Controller for handling feedback part of the competition
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

    @PutMapping("/{id}/close-assessment")
    public RestResult<Void> closeAssessment(@PathVariable("id") final Long id) {
        return competitionService.closeAssessment(id).toPutResponse();
    }

    @GetMapping("/{competitionId}/unsuccessful-applications")
    public RestResult<ApplicationPageResource> findUnsuccessfulApplications(@PathVariable("competitionId") final Long competitionId,
                                                                            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                                            @RequestParam(value = "sort", defaultValue = DEFAULT_SORT_BY) String sortField) {

        return competitionService.findUnsuccessfulApplications(competitionId, pageIndex, pageSize, sortField).toGetResponse();
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
